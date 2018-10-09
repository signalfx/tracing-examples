package com.signalfx.tracing.examples;

import io.opencensus.common.Scope;
import io.opencensus.trace.*;
import io.opencensus.trace.propagation.SpanContextParseException;
import io.opencensus.trace.propagation.TextFormat;
import io.opencensus.trace.samplers.Samplers;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * This filter will extract any spans in the request header and use it as a
 * parent context.
 */
public class TracingFilter extends OncePerRequestFilter {

    /**
     * The globally registered tracer
     */
    private static final Tracer tracer = Tracing.getTracer();

    /**
     * The propagation format for deserializing the span
     */
    private static final TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();

    /**
     * The getter that will extract the span from the incoming request
     */
    private static final TextFormat.Getter<HttpServletRequest> getter = new TextFormat.Getter<HttpServletRequest>() {
        @Override
        public String get(HttpServletRequest httpRequest, String s) {
            return httpRequest.getHeader(s);
        }
    };

    /**
     * Use the text format and getter to extract a serialized span from the
     * request, and set that as the parent context for the request.
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SpanContext spanContext;
        SpanBuilder spanBuilder;

        String spanName = "doFilterInternal_" + request.getRequestURI();

        try {
            // extract the span context if its present and create a builder with
            // it as the parent context.
            // the span kind is set to SERVER since the request is received here
            spanContext = textFormat.extract(request, getter);
            spanBuilder = tracer.spanBuilderWithRemoteParent(spanName, spanContext)
                                .setSpanKind(Span.Kind.SERVER);
        } catch (SpanContextParseException e) {
            // create a normal spanBuilder if there was no span to extract
            spanBuilder = tracer.spanBuilder(spanName);
        }

        // Start a new span for the request
        Span span = spanBuilder.setRecordEvents(true)
                .setSampler(Samplers.alwaysSample()).startSpan();
        span.putAttribute("http.method", AttributeValue.stringAttributeValue(request.getMethod()));

        // continue the filter chain and close the span
        try (Scope s = tracer.withSpan(span)) {
            filterChain.doFilter(request, response);
        } finally {
            span.end();
        }
    }
}
