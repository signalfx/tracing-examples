package com.splunk.tracing.otel.examples.javaagent;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import redis.clients.jedis.Jedis;

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

        String url = argv[0];

        App app = new App();
        app.doFetchAndSet(url);
    }

    private void doFetchAndSet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();


        Tracer tracer = OpenTelemetry.getGlobalTracer("sample");

        // This span acts as a root span that envelops the sets of spans generated
        // by both the HTTP call (using the OKHttp auto-instrumentation) and the
        // Redis set action (using the redis-jedis auto-instrumentation).  If you
        // did not have this root span that is manually created, you would instead
        // see two independent traces for the HTTP get and the Redis set.
        Span span = tracer.spanBuilder("fetch-and-set").startSpan();
        try (Scope sc = span.makeCurrent()) {
            String respBody;
            try {
                Response response = httpClient.newCall(request).execute();
                respBody = response.body().string();
            } catch (IOException e) {
                System.out.println("Error: " + e);
                return;
            }

            executor.submit(() -> redisClient.set(url, respBody));
        } catch (Throwable e) {
            span.recordException(e);
            System.out.println(e);
        } finally {
            span.end();
        }


        try {
            executor.shutdown();
            executor.awaitTermination(3000, TimeUnit.MILLISECONDS);
            redisClient.close();
            // Give the tracer time to flush the spans in the sender thread.  In
            // long-running apps this is generally unnecessary but unfortunately there is no
            // OpenTelemetry API method for closing/stopping a tracer.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return;
        }
    }
}
