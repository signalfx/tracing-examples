package com.signalfx.tracing.examples.brave;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.sampler.CountingSampler;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.Request;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * This is a basic app that demonstrates the use of the Zipkin Brave Java client with SignalFx.
 * This will estimate the value of pi using Leibniz's formula and shows the use of a parent span
 * with multiple sibling child spans.  The child spans could just as well be created on a remote
 * process, with the parent span propagated by the inject/extract methods on the Tracer.
 */
public class App {

  // constants used by the example application logic
  static final int NUM_PARTS = 3;
  static final long PART_TERMS = 100000000;

  public static void main(String[] args) {
    // Here we instantiate our TracingHelper class and get the tracer from it.  Normally this would
    // be done by your DI framework and the resulting tracer injected to each class that needs it.
    TracingHelper tracingHelper = new TracingHelper();
    Tracer tracer = tracingHelper.getTracer();

    // Here we create and start a root span that will be the parent of the other spans. We have
    // used the `startScopedSpan` method instead of `nextSpan` so that it will both create a new
    // Span and make it in the current scope.  The resulting object is of type ScopedSpan which
    // exposes most of the same interface as Span itself, the main difference being that ScopedSpan
    // is not thread-safe.
    //
    // We are calling the span "root" in this case but the name can be anything.
    ScopedSpan parentSpan = tracer.startScopedSpan("root");

    // Here we are setting a tag on the parent span giving more information about the current
    // computation.  This would be more meaningful if the value were variable across instances
    // of the span.
    parentSpan.tag("totalTerms", String.valueOf(NUM_PARTS * PART_TERMS));

    try {
      // Here we will create an ExecutorService to do some parallel processing.  Brave comes
      // with a decorator that wraps ExecutorService so that the current trace context is correctly
      // propagated to the threads used by the executor.
      CurrentTraceContext context = tracingHelper.tracing.currentTraceContext();
      ExecutorService executor = context.executorService(Executors.newFixedThreadPool(NUM_PARTS));
      // This is part of our example app logic so that we can show passing spans across thread
      // boundaries.
      CompletionService<Double> completionService = new ExecutorCompletionService(executor);

      for (int i = 0; i < NUM_PARTS; i++) {
        int part = i;
        // The code inside the submitted lambda will run in a separate thread.  Keep in mind
        // that a span is normally considered in scope for a single thread, but because we used the
        // decorator to ExecutorService, the scope will be propagated across threads.
        completionService.submit(() -> {
          long termStart = part * PART_TERMS;

          // Since we used the ExecutorService decorator above, this will automatically set the parent span
          // of this newly created span to our parent span from above.
          ScopedSpan childSpan = tracer.startScopedSpan("part");

          // Set a tag on the current span so that we can distinguish individual spans.
          // Tags are simple key/value pairs, where the key must be a string.
          childSpan.tag("part", String.valueOf(part));

          // Do the application logic and log any errors to the child span.
          try {
            return calculatePart(termStart, termStart + PART_TERMS);
          } catch (Exception e) {
            // This will set a tag on the span called "error" with the exception message as the value.
            childSpan.error(e);
            throw e;
          } finally {
            // This will finish the span and clear it from the current scope.
            childSpan.finish();
          }
        });
      }

      // Assemble all of the parts we generated in the child workers.
      double piEstimate = 0;
      for (int i = 0; i < NUM_PARTS; i++) {
        piEstimate += completionService.take().get() * 4.0;
      }

      executor.shutdown();

      if ("yes".equals(System.getProperty("throwError"))) {
        throw new Exception("Some random error happened");
      }

      System.out.println("pi ~= " + piEstimate);
    // Handle exceptions in our application logic.  We have access to the span object here
    } catch (Exception e) {
      // This will set a tag on the span called "error" with the exception message as the value.
      parentSpan.error(e);
    } finally {
      // Since parentSpan is actually a ScopedSpan, this will both finish the span and clear it from
      // the current scope.
      parentSpan.finish();
    }

    // We have to close the various components of the tracer so that all spans are flushed before
    // the application quits.  Normally if you were using a DI framework, this could be part
    // of the cleanup code for the singleton tracer instance.
    tracingHelper.close();
  }

  /**
   * Calculate a chunk of terms in the Leibniz pi estimation.  There is no tracing specific logic
   * here, nor should there be.
   */
  private static double calculatePart(long termStart, long termEnd) {
    double partialSum = 0;
    for (long k = termStart; k < termEnd; k++) {
      partialSum += ((k % 2 == 0) ? 1 : -1) / (double) (((2 * k) + 1));
    }
    return partialSum;
  }

  /**
   * A helper class that encapsulates all of the Brave objects that need to be created and cleaned up
   * upon shutdown.  If you are using a DI framework, this logic would be used by that
   * to create a single instance of the tracer and inject it to every class that needs it.  Be sure
   * to incorporate the logic in the close method to your DI framework's shutdown logic.
   */
  private static class TracingHelper {

    // We need to keep references to all of these components because they have to be closed upon
    // shutdown in a certain order to avoid losing spans.
    private AsyncReporter reporter;
    private Tracing tracing;
    private OkHttpSender sender;

    TracingHelper() {
      // The ingest url is where the span data will be sent, which can normally just be the default
      // value of this property.
      String ingestUrl = System.getProperty("ingestUrl", "https://ingest.signalfx.com");
      // This would be your organization's SignalFx access token, accessed in whatever manner most
      // appropriate to your environment.
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

      this.sender = senderBuilder.build();
      this.reporter = AsyncReporter.create(sender);

      // Create the Tracing instance from which we obtain the tracer instance
      this.tracing = Tracing.newBuilder()
          // This sets the name of the local application and will be fairly prominent in the Zipkin UI.
          .localServiceName("signalfx-zipkin-brave-example")
          .spanReporter(reporter)
          // Use a sampler that always reports spans.  You can swap this out for other samplers.
          .sampler(CountingSampler.create(1.0f))
          .build();
    }

    /**
     * Return the tracer instance from the Tracing object.  This is what spans are created through.
     */
    public Tracer getTracer() {
      return tracing.tracer();
    }

    /**
     * This might be part of the shutdown logic if using a DI framework.  It should be called one way
     * or another though.
     */
    public void close() {
      tracing.close();
      reporter.close();
      sender.close();
    }
  }
}
