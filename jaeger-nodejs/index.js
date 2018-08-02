const fetch = require("node-fetch");
const { initTracer } = require("jaeger-client");
const opentracing = require("opentracing");
const URL = require("url");

// This function creates and initializes a Jaeger tracer that is compatible
// with OpenTracing.  This should only be created once per process and the
// instance returned should be shared everywhere it is needed.
function createTracer() {
  // You can use the default value in most cases unless you are using our
  // metricproxy.
  let ingestUrl = URL.parse(process.env.SIGNALFX_INGEST_URL || "https://ingest.signalfx.com/v1/trace");

  const accessToken = process.env.SIGNALFX_ACCESS_TOKEN;
  if (!accessToken) {
    console.error("You must set the envvar SIGNALFX_ACCESS_TOKEN");
    process.exit(1);
  }

  const config = {
    // This is the service name that will be used by default for the "local"
    // side of spans emitted from this tracer.
    serviceName: "signalfx-jaeger-node-example",
    // This configures the tracer to send every span generated.  You can also
    // use a probabalistic sampler, among many others.
    // See https://github.com/jaegertracing/jaeger-client-node/tree/master/src/samplers
    sampler: {
      type: "const",
      param: 1
    },
    // This will configure the tracer to send spans via HTTP to the configured
    // ingestUrl.  The spans are sent in Jaeger's Thrift format.
    reporter: {
      logSpans: true,
      collectorEndpoint: ingestUrl,
      // You authenticate to SignalFx using Basic auth with a username of
      // "auth" and a password that is your access token.
      username: "auth",
      password: process.env.SIGNALFX_ACCESS_TOKEN
    }
  };

  const options = {
    logger: console
  };

  const tracer = initTracer(config, options);
  opentracing.initGlobalTracer(tracer);

  return tracer;
}

// The tracer should only be instantiated once in the lifetime of the
// application.  It should be treated as a singleton and imported in whatever
// module needs it.
const tracer = createTracer();

// This is a traced wrapper of fetch.  It isn't terribly flexible as it doesn't
// allow for any customization of the HTTP requests and assumes that you want
// the response interpreted as JSON.  But it shows the basic technique of
// starting a span, given a context, and attaching tags and events to it.
// "context", if not null, is the parent span of the current request.
function fetchTracedJSON(context, url) {
  const span = tracer.startSpan(URL.parse(url).hostname, context ? { childOf: context } : undefined);
  span.setTag(opentracing.Tags.HTTP_URL, url);
  span.setTag(opentracing.Tags.HTTP_METHOD, "GET");
  span.setTag(opentracing.Tags.SPAN_KIND, opentracing.Tags.SPAN_KIND_RPC_CLIENT);

  return fetch(url)
    .then(resp => {
      span.setTag(opentracing.Tags.HTTP_STATUS_CODE, resp.status);

      // You can set this envvar to something to simulate an error event on
      // the span.
      if (process.env.SIMULATE_ERROR) {
        throw new Error("Could not get body of response");
      }
      // The span isn't logically finished until the body is done streaming.
      return resp.json();
    })
    .catch(err => {
      span.setTag(opentracing.Tags.ERROR, true);
      span.log({
        event: "error",
        message: err.toString()
      });

      throw err;
    })
    .finally(() => {
      span.finish();
    });
}

function getBiggestGainers(context) {
  return fetchTracedJSON(context, "https://api.iextrading.com/1.0/stock/market/list/gainers");
}

function getIndustryOfStock(context, stock) {
  return fetchTracedJSON(context, "https://api.iextrading.com/1.0/stock/" + stock + "/company").then(
    company => company.industry
  );
}

function countIndustries(industries) {
  const counts = {};
  for (const i in industries) {
    const ind = industries[i];
    if (ind in counts) {
      counts[ind]++;
    } else {
      counts[ind] = 1;
    }
  }
  return counts;
}

// Look up the "biggest gainers" in the stock market currently and then find
// which industries they belong to and print out a count of those industries to
// the console.  This involves several HTTP requests, all of which are traced.
function printMostGainingIndustries() {
  // Create the "root" span of this line of processing.
  const rootSpan = tracer.startSpan("industries-of-biggest-gainers");

  // Span context generally must be passed explicitly in Javascript since
  // everything runs in a single thread and is highly asynchronous.  It is
  // possible to use AsyncListeners and continuation-local-storage to propagate
  // the context implicitly, but the Jaeger tracer and OpenTracing do not
  // directly support that currently.
  return getBiggestGainers(rootSpan)
    .then(gainers => {
      rootSpan.setTag("numResults", gainers.length);
      return Promise.all(gainers.map(g => g.symbol).map(sym => getIndustryOfStock(rootSpan, sym)));
    })
    .then(countIndustries)
    .then(industryCounts => {
      for (k in industryCounts) {
        console.log(k + ": " + industryCounts[k]);
      }
    })
    .catch(err => {
      // You should always set the "error" tag on spans that see errors.
      rootSpan.setTag(opentracing.Tags.ERROR, true);
      rootSpan.log({
        event: "error",
        message: err.toString()
      });
    })
    .finally(() => {
      // We need to make sure we finish the span on both the success and error
      // paths.
      rootSpan.finish();
    });
}

printMostGainingIndustries().finally(() => {
  // Make sure to close the tracer before the application finishes so that
  // spans are not lost.
  tracer.close();
});
