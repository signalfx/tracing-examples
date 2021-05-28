package com.signalfx.tracing.examples.javaagent;

import com.signalfx.tracing.api.Trace;
import io.opentelemetry.opentracingshim.OpenTracingShim;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.opentracing.log.Fields.ERROR_OBJECT;

/**
 * This is a very simple demo app that shows the use of both the Java agent's
 * auto-instrumentation combined with manual tracing.
 * <p>
 * The app accepts a single CLI argument that should be a URL.  The app will go
 * out and fetch that URL with the OkHttp client (auto-instrumented) and then
 * store it into a local Redis database using the Jedis client library (also
 * auto-instrumented).  Both of these operations are performed within a root
 * parent span called "fetch-and-set" that is manually created in the code
 * below.
 * <p>
 * See the example for the Jaeger Java tracer for more detailed information on
 * the use of the OpenTracing API.
 */
public class App {
    private final OkHttpClient httpClient;
    private final Jedis redisClient;
    private final ExecutorService executor;

    App() {
        String redisHost = System.getenv("REDIS_HOSTNAME");
        if (redisHost == null) {
            redisHost = "localhost";
        }
        this.redisClient = new Jedis(redisHost);
        this.httpClient = new OkHttpClient.Builder().build();
        executor = Executors.newSingleThreadExecutor();
    }

    public static void main(String[] argv) {
        if (argv.length == 0) {
            System.out.println("Please specify a URL to fetch");
            System.exit(1);
        }

        GlobalTracer.registerIfAbsent(OpenTracingShim.createTracerShim());

        String url = argv[0];

        App app = new App();
        app.doFetchAndSet(url);
    }

    private void doFetchAndSet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        // This span acts as a root span that envelops the sets of spans generated
        // by both the HTTP call (using the OKHttp auto-instrumentation) and the
        // Redis set action (using the redis-jedis auto-instrumentation).  If you
        // did not have this root span that is manaully created, you would instead
        // see two independent traces for the HTTP get and the Redis set.
        // The Java agent sets the GlobalTracer instance before the application's main method is called.
        Span span = GlobalTracer.get().buildSpan("fetch-and-set").start();
        try (Scope sc = GlobalTracer.get().scopeManager().activate(span)) {
            String respBody;
            try {
                Response response = httpClient.newCall(request).execute();
                respBody = response.body().string();
            } catch (IOException e) {
                System.out.println("Error: " + e);
                return;
            }

            setInRedisWithThread(url, respBody);
        } catch (Throwable e) {
            Tags.ERROR.set(span, true);
            span.log(Collections.singletonMap(ERROR_OBJECT, e));
            System.out.println(e);
        } finally {
            span.finish();
        }


        try {
            executor.shutdown();
            executor.awaitTermination(3000, TimeUnit.MILLISECONDS);
            redisClient.close();
            // Give the tracer time to flush the spans in the sender thread.  In
            // long-running apps this is generally unnecessary but unfortunately there is no
            // OpenTracing interface method for closing/stopping a tracer.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return;
        }
    }

    /*
     * This annotation will generate a span that wraps the auto-generated Redis SET command span that is
     * run within a separate thread.  This is a fairly
     * trivial example that doesn't add much value but at least shows the use of the Trace annotation.
     */
    @Trace(operationName = "setInRedis")
    public void setInRedisWithThread(String url, String value) {
        // This accesses the span created by the @Trace annotation and adds a tag to it.
        Span activeSpan = GlobalTracer.get().scopeManager().activeSpan();
        activeSpan.setTag("my-tag", "my-value");

        executor.submit(() -> redisClient.set(url, value));
    }
}
