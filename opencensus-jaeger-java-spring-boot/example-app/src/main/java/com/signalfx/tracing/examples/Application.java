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

    @RequestMapping("/test")
    public String test() throws Exception {

        // start parent span
        Span span = buildSpan("GET /test");
        span.addAnnotation("Parent span");

        try (Scope ws = tracer.withSpan(span)) {

            childProcess();
        }

        String result = "";
        try (Scope ws = tracer.withSpan(span)) {

            result = remoteProcess();
        }

        span.end();
        return result;
    }

    public void childProcess() {
        Span span = buildSpan("childProcess");
        span.addAnnotation("Local child span");
        span.end();
    }

    public String remoteProcess() {

        Span span = buildSpan("remoteProcess");
        span.addAnnotation("Remote child span");

        String url = "http://localhost:8099/count";
        String result = "";

        try (Scope ws = tracer.withSpan(span)) {

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            textFormat.inject(span.getContext(), conn, setter);
            conn.setRequestMethod(HttpMethod.GET.name());

            // read the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();

        } catch (Exception e) {
            span.setStatus(Status.ABORTED);
            span.addAnnotation("Error calling endpoint");
            result = "";
        }

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
