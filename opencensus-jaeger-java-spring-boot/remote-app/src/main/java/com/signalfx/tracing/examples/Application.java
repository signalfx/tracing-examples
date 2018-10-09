package com.signalfx.tracing.examples;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.opencensus.common.Scope;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.propagation.TextFormat;
import io.opencensus.trace.samplers.Samplers;
import io.opencensus.trace.Span;
import io.opencensus.trace.SpanBuilder;
import io.opencensus.trace.Status;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;

/**
 * This app simply keeps track of the number of times a request has been made
 * to /count.
 */
@SpringBootApplication
@RestController
public class Application implements WebMvcConfigurer {

    /**
     * The globally registered tracer
     */
    private static final Tracer tracer = Tracing.getTracer();

    /**
     * This keeps track of the number of requests made
     */
    private static int requestCount = 0;

    /**
     * Start a span and call a method that increments a count
     *
     * @return the result of the called function
     */
    @RequestMapping("/count")
    public String count() throws Exception {
        // start parent span
        Span span = buildSpan("count");
        span.putAttribute("http.method", AttributeValue.stringAttributeValue("GET"));
        span.addAnnotation("Count endpoint");

        // Give the scope as a resource for the called function
        try (Scope ws = tracer.withSpan(span)) {
            return getRequestCount();
        } finally {
            span.end();
        }
    }

    /**
     * This functions starts a new span, annotates it with the count prior to
     * the latest request, and returns a string with the new number of requests.
     *
     * @return a string with the total number of requests
     */
    public String getRequestCount() {
        // annotate the span with the old request count
        Span span = buildSpan("getRequestCount");
        span.putAttribute("number_of_requests_so_far", AttributeValue.longAttributeValue(requestCount));

        // update the request count and create a string with it
        requestCount++;
        String result = "Total number of requests: " + requestCount;

        span.end();
        return result;
    }

    /**
     * Use the tracer span builder to create and start a new span
     *
     * @param name the span's name
     * @return the new span
     */
    public Span buildSpan(String name) {
        Span span = tracer.spanBuilder(name)
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startSpan();
        return span;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
