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

import org.apache.commons.io.IOUtils;

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
 * This Spring Boot app uses the OpenCensus Jaeger exporter with SignalFx.
 * Making a request to /test will cause a request to be sent to another app
 * at localhost:8099/count with an injected span. The response from the remote
 * app is then displayed. If it can't be reached, the span is tagged with an
 * error before being sent out, and no text is displayed.
 */
@SpringBootApplication
@RestController
public class Application implements WebMvcConfigurer {

    /**
     * The global tracer instance
     */
    private static final Tracer tracer = Tracing.getTracer();

    /**
     * The propagation format for injecting a span into a HTTP request
     */
    private static final TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();

    /**
     * Setter used to inject the header for a HttpURLConnection
     */
    private static final TextFormat.Setter setter = new TextFormat.Setter<HttpURLConnection>() {
        public void put(HttpURLConnection carrier, String key, String value) {
            carrier.setRequestProperty(key, value);
        }
    };

    /**
     * Starts a parent span with two child spans: one local and one on for a
     * remote process
     *
     * @return text from the remote request or an empty string
     */
    @RequestMapping("/test")
    public String test() throws Exception {
        // start parent span
        Span span = buildSpan("test");
        span.addAnnotation("Parent span");

        // tag the span with the http method
        span.putAttribute("http.method", AttributeValue.stringAttributeValue("GET"));

        // trying with this resource lets any new span be nested inside this one
        try (Scope ws = tracer.withSpan(span)) {
            childProcess();
        }

        // any new spans created by remoteProcess will be nested in this one,
        // and when leaving the block the parent span is closed as well.
        try (Scope ws = tracer.withSpan(span)) {
            return remoteProcess();
        } finally {
            span.end();
        }
    }

    /**
     * Simple span that doesn't really do any work
     */
    public void childProcess() {
        Span span = buildSpan("childProcess");
        span.addAnnotation("Local child span");
        span.end();
    }

    /**
     * Makes a request to a remote app
     *
     * @return response text from the remote or an empty string
     */
    public String remoteProcess() {
        // start a new span and annotate it
        // mark this span as a "client" kind since it is the outbound request
        Span span = buildSpan("remoteProcess", Span.Kind.CLIENT);
        span.addAnnotation("Remote child span");

        // the url to request
        String url = "http://localhost:8099/count";

        try (Scope ws = tracer.withSpan(span)) {
            // Make the request and inject the current span into the header.
            // This will allow the request endpoint to extract the span and
            // register any new spans as children of this span.
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            textFormat.inject(span.getContext(), conn, setter);
            conn.setRequestMethod(HttpMethod.GET.name());

            // read the response
            return IOUtils.toString(conn.getInputStream(), "UTF-8");
        } catch (Exception e) {
            // the request failed for some reason, just set the status and a message
            span.setStatus(Status.ABORTED);
            span.addAnnotation("Error calling endpoint");
            span.putAttribute("error", AttributeValue.booleanAttributeValue(true));
        } finally {
            // close the span once finished
            span.end();
        }

        return "";
    }

    /**
     * This uses the tracer spanBuilder to create and start a span
     *
     * @param name Name of the new span
     * @return the newly created span
     */
    public Span buildSpan(String name) {
        // build a span using the default span builder and set it to always sample
        Span span = tracer.spanBuilder(name)
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startSpan();

        return span;
    }

    /**
     * This uses the tracer spanBuilder to create and start a span
     *
     * @param name Name of the new span
     * @param kind The kind of span, client or server
     * @return the newly created span
     */
    public Span buildSpan(String name, Span.Kind kind) {
        // build a span using the default span builder and set it to always sample
        Span span = tracer.spanBuilder(name)
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .setSpanKind(kind)
            .startSpan();

        return span;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
