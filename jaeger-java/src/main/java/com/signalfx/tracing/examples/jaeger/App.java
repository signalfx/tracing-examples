package com.signalfx.tracing.examples.jaeger;

import io.jaegertracing.senders.zipkin.Zipkin2Reporter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import io.jaegertracing.samplers.ConstSampler;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import okhttp3.Request;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * This is a basic app that demonstrates the use of the Jaeger Java client with SignalFx.  This will
 * estimate the value of pi using Leibniz's formula and shows the use of a parent span with multiple
 * sibling child spans.  The child spans could just as well be created on a remote process, with the
 * parent span propagated by the inject/extract methods on the Tracer.
 */
public class App
{
    /**
     * Create a Jaeger tracer instance that is configured to send span data to SignalFx.  This is
     * intended to be called once.  If you are using a DI framework, this logic would be used by
     * that to create a single instance of the tracer and inject it to every class that needs it.
     */
    private static io.opentracing.Tracer createTracer() {
        String ingestUrl = System.getProperty("ingestUrl", "https://ingest.signalfx.com");
        String accessToken = System.getProperty("accessToken");

        // Build the sender that does the HTTP request containing spans to our ingest server.
        OkHttpSender.Builder senderBuilder = OkHttpSender.newBuilder()
                .compressionEnabled(true)
                .endpoint(ingestUrl + "/v1/trace");

        // Add an interceptor to inject the SignalFx X-SF-Token auth header.
        senderBuilder.clientBuilder().addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("X-SF-Token", accessToken)
                    .build();

            return chain.proceed(request);
        });

        OkHttpSender sender = senderBuilder.build();

        // Build the Jaeger Tracer instance, which implements the opentracing Tracer interface.
        io.opentracing.Tracer tracer = new io.jaegertracing.Tracer.Builder("signalfx-jaeger-java-example")
                // This configures the tracer to send all spans, but you will probably want to use
                // something less verbose.
                .withSampler(new ConstSampler(true))
                // Configure the tracer to send spans in the Zipkin V2 JSON format instead of the
                // default Jaeger protocol, which we do not support.
                .withReporter(new Zipkin2Reporter(AsyncReporter.create(sender)))
                .build();

        // It is considered best practice to at least register the GlobalTracer instance, even if you
        // don't generally use it.
        GlobalTracer.register(tracer);

        return tracer;
    }

    // constants used by the example application logic
    static final int NUM_PARTS = 3;
    static final long PART_TERMS = 100000000;

    public static void main(String[] args)
    {
        // Create a single instance of a Jaeger tracer that will be used throughout the application.
        // If you are using a DI framework, you should rely on that as much as possible to provide
        // this instance.  Here we are defining the tracer as an OpenTracing tracer, since it implements
        // that interface.  You should generally use the OpenTracing interface where possible to make
        // it potentially easier to swap out tracers in the future.
        io.opentracing.Tracer tracer = createTracer();

        // Here we create and start a root span that will be the parent of the other spans. We have
        // used the `start` method instead of `startActive` so that we can get a reference to the span
        // outside of the main try/catch block that contains our application logic.  We need that in
        // order to log exceptions from the application to the span.
        // We are calling the span "root" in this case but the name can be anything.
        Span parentSpan = tracer.buildSpan("root").start();

        // Here we are setting a tag on the parent span giving more information about the current
        // computation.  This would be more meaningful if the value were variable across instances
        // of the span.
        parentSpan.setTag("totalTerms", NUM_PARTS * PART_TERMS);

        // We need to set the span to the active span for this thread.  By using the try-with-resource
        // construct, the span will be deactivated automatically when the block exits and the scope
        // is closed.  By setting the second arg of `activate` to `false`, we are telling the tracer
        // to not finish our span automatically when this scope is closed, since we do this manually
        // below in the `finally` block.
        try (Scope parentScope = tracer.scopeManager().activate(parentSpan, false)) {
            // This is part of our example app logic so that we can show passing spans across thread
            // boundaries.
            ExecutorService executor = Executors.newFixedThreadPool(NUM_PARTS);
            CompletionService<Double> completionService =
                    new ExecutorCompletionService(executor);

            for (int i = 0; i < NUM_PARTS; i++) {
                int part = i;
                // The code inside the submitted lambda will run in a separate thread.  Keep in mind
                // that a span is considered active for a single thread.
                completionService.submit(() -> {
                    // Use the try-with-resource pattern for creating, starting, and activating a
                    // span child span in the current thread.  We must specify the parent span since
                    // these spans are all part of a single trace.  Here we limit exception handling
                    // to a nested block so we don't need access to the span outside of this block,
                    // unlike what we did with the parent span.
                    // If you were continuing logic asynchronously from the parent span in some kind
                    // of callback, you could reactivate that span in the callback thread (potentially
                    // the same thread) by calling `tracer.scopeManager.activate(parentSpan, true/false)`.
                    // We are, however, wanting to start a completely new child span here.
                    try (Scope childScope = tracer.buildSpan("part").asChildOf(parentSpan).startActive(true)) {
                        long termStart = part * PART_TERMS;
                        // Set a tag on the current span so that we can distinguish individual spans.
                        // Tags are simple key/value pairs, where the key must be a string.
                        childScope.span().setTag("part", part);

                        // Do the application logic and log any errors to the child span.
                        try {
                            return calculatePart(termStart, termStart + PART_TERMS);
                        } catch(Exception e) {
                            // Set the error tag on the child span
                            Tags.ERROR.set(childScope.span(), true);

                            // Log the exception on the span using a Map.
                            Map<String, Object> logFields = new HashMap();
                            logFields.put(Fields.EVENT, "error");
                            logFields.put(Fields.ERROR_OBJECT, e);
                            logFields.put(Fields.MESSAGE, e.getMessage());
                            parentSpan.log(logFields);
                            throw e;
                        }
                        // When this block exits, the active span will be finished (due to the `true`
                        // parameter to the `startActive` method above), and the scope will be closed
                        // and deactivated on the current thread.
                    }
                });
            }

            // Assemble all of the parts we generated in the child workers.
            double piEstimate = 0;
            for (int i = 0; i < NUM_PARTS; i++) {
                piEstimate += completionService.take().get() * 4.0;
            }

            executor.shutdown();

            System.out.println("pi ~= " + piEstimate);
        // Handle exceptions in our application logic.  We have access to the span object here
        } catch (InterruptedException | ExecutionException e) {
            // Set the error tag on the parent span
            Tags.ERROR.set(parentSpan, true);

            // Log the error out as a String in this case.  It is probably better to use a Map instead
            // as done with the child spans.
            parentSpan.log(e.getMessage());
        } finally {
            // Since we passed `false` to the `activate(parentSpan, false)` call above, we MUST
            // manually finish the span.
            parentSpan.finish();
        }

        // This is a hack to make sure the tracer flushes and sends the spans since this is a very
        // short-lived application.  Normally if you were using a DI framework, this could be part
        // of the cleanup code for the singleton tracer instance.
        ((io.jaegertracing.Tracer)(tracer)).close();
    }

    /**
     * Calculate a chunk of terms in the Leibniz pi estimation.  There is no tracing specific logic
     * here, nor should there be.
     */
    private static double calculatePart(long termStart, long termEnd) {
        double partialSum = 0;
        for (long k = termStart; k < termEnd; k++) {
            partialSum += ((k % 2 == 0) ? 1 : -1) / (double)(((2 * k) + 1));
        }
        return partialSum;
    }
}
