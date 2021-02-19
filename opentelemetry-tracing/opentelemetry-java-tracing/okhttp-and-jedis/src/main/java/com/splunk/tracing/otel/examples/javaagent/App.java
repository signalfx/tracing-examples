package com.splunk.tracing.otel.examples.javaagent;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.extension.annotations.WithSpan;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        System.out.println("Example complete");
    }

    // This span acts as a root span that is the parent of the spans generated
    // by both the HTTP call (using the OKHttp auto-instrumentation) and the
    // Redis set action (using the redis-jedis auto-instrumentation).  If you
    // did not have this root span that is manually created, you would instead
    // see two independent traces for the HTTP get and the Redis set.
    @WithSpan("fetch-and-set")
    private void doFetchAndSet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        //this will get the current span from the Context, which was created by the auto-instrumentation agent based
        // on the annotation on the method.
        Span span = Span.current();
        try {
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
            span.setStatus(StatusCode.ERROR, e.getMessage());
            e.printStackTrace();
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
