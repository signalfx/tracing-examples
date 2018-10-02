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



@SpringBootApplication
@RestController
public class Application implements WebMvcConfigurer {

    private static final Tracer tracer = Tracing.getTracer();
    private static final TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();
    private static final TextFormat.Setter setter = new TextFormat.Setter<HttpURLConnection>() {
        public void put(HttpURLConnection carrier, String key, String value) {
            carrier.setRequestProperty(key, value);
        }
    };

    private static int requestCount = 0;


    @RequestMapping("/count")
    public String count() throws Exception {

        // start parent span
        Span span = buildSpan("request count");
        span.addAnnotation("Count endpoint");

        String result = "";

        try (Scope ws = tracer.withSpan(span)) {

            result = getRequestCount();
        }

        span.end();
        return result;
    }

    public String getRequestCount() {
        Span span = buildSpan("getRequestCount");
        span.addAnnotation("number of requests so far, not counting this one: " + requestCount);

        requestCount++;
        String result = "Total number of requests: " + requestCount;

        span.end();
        
        return result;
    }

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
