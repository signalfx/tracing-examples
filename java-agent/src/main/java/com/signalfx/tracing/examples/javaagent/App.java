package com.signalfx.tracing.examples.javaagent;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Collections;

import static io.opentracing.log.Fields.ERROR_OBJECT;

/**
 * This is a very simple demo app that shows the use of both the Java agent's
 * auto-instrumentation combined with manual tracing.
 *
 * The app accepts a single CLI argument that should be a URL.  The app will go
 * out and fetch that URL with the OkHttp client (auto-instrumented) and then
 * store it into a local Redis database using the Jedis client library (also
 * auto-instrumented).  Both of these operations are performed within a root
 * parent span called "fetch-and-set" that is manually created in the code
 * below.
 *
 * See the example for the Jaeger Java tracer for more detailed information on
 * the use of the tracer.
 */
public class App {
  public static void main(String[] argv) {
    // The Java agent sets the global tracer instance before this method is
    // called so it is safe to grab and use here.
    Tracer tracer = GlobalTracer.get();

    OkHttpClient client = new OkHttpClient.Builder().build();

    if (argv.length == 0) {
        System.out.println("Please specify a URL to fetch");
        System.exit(1);
    }

    String url = argv[0];

    Request request = new Request.Builder()
            .url(url)
            .build();

    Jedis redisClient = new Jedis("localhost");

    // This span acts as a root span that envelops the sets of spans generated
    // by both the HTTP call (using the OKHttp auto-instrumentation) and the
    // Redis set action (using the redis-jedis auto-instrumentation).  If you
    // did not have this root span that is manaully created, you would instead
    // see two independent traces for the HTTP get and the Redis set.
    Span span = tracer.buildSpan("fetch-and-set").start();
    try (Scope sc = tracer.scopeManager().activate(span, false)) {
      String respBody;
      try {
        Response response = client.newCall(request).execute();
        respBody = response.body().string();
      } catch (IOException e) {
        System.out.println("Error: " + e);
        return;
      }

      redisClient.set(url, respBody);
    } catch (Throwable e) {
      Tags.ERROR.set(span, true);
      span.log(Collections.singletonMap(ERROR_OBJECT, e));
      System.out.println(e);
    } finally {
      span.finish();
    }

    redisClient.close();

    // Give the tracer time to flush the spans in the sender thread.  In
    // long-running apps this is unnecessary but unfortunately there is no
    // OpenTracing interface method for closing/stopping a tracer.
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      return;
    }
  }
}
