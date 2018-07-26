const {
  Tracer,
  BatchRecorder,
  ExplicitContext,
  jsonEncoder: { JSON_V2 }
} = require("zipkin");

const { HttpLogger } = require("zipkin-transport-http");
const fetch = require("node-fetch");
const wrapFetch = require("zipkin-instrumentation-fetch");

function createTracer() {
  // Setup the tracer to use http and explicit trace context.
  const tracer = new Tracer({
    ctxImpl: new ExplicitContext(),
    recorder: new BatchRecorder({
      logger: new HttpLogger({
        endpoint: "https://ingest.signalfx.com/v1/trace",
        jsonEncoder: JSON_V2,
        // For this example, you should provide your organization's access
        // token as an environment variable, but in a real app this can be
        // gotten some other way.
        headers: { "X-SF-Token": process.env.SFX_ACCESS_TOKEN }
      })
    }),
    // This is what will show up as the service name for any local spans
    // generated.
    localServiceName: "signalfx-js-zipkin-example"
  });

  return tracer;
}

// The tracer should only be instantiated once in the lifetime of the
// application.  It should be treated as a singleton and imported in whatever
// module needs it.
const tracer = createTracer();

// This wraps the fetch http client using Zipkin's instrumentation.
const iexFetch = wrapFetch(fetch, { tracer, serviceName: "iex" });

function getBiggestGainers() {
  return iexFetch(
    "https://api.iextrading.com/1.0/stock/market/list/gainers"
  ).then(res => res.json());
}

function getIndustryOfStock(stock) {
  return iexFetch("https://api.iextrading.com/1.0/stock/" + stock + "/company")
    .then(res => res.json())
    .then(company => company.industry);
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

function printMostGainingIndustries() {
  tracer.local("industries-of-biggest-gainers", () => {
    // Save the tracer id so that we can set it back on the tracer in our
    // promise callbacks so that the parent/child relationship between
    // spans is preserved.
    const id = tracer.id;

    // We need to return this chain of promises so that the tracer keeps the
    // span "open" until they all resolve, so that we can add metadata to it
    // below.
    return getBiggestGainers()
      .then(gainers =>
        // Reset the parent span id using `letId` so that all of the subsequent
        // trace activity in this promise callback has the proper context.
        tracer.letId(id, () => {
          tracer.recordBinary("numResults", gainers.length);
          return Promise.all(
            gainers.map(g => g.symbol).map(getIndustryOfStock)
          );
        })
      )
      .then(countIndustries)
      .then(industryCounts => {
        for (k in industryCounts) {
          console.log(k + ": " + industryCounts[k]);
        }
      });
  });
}

printMostGainingIndustries();

// Ensure spans have enough time to flush before the program ends.
setTimeout(() => {}, 1000);
