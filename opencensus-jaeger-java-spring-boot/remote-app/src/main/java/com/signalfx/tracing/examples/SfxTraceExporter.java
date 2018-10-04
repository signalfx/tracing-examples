package com.signalfx.tracing.examples;

import io.opencensus.exporter.trace.jaeger.JaegerTraceExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import com.uber.jaeger.senders.HttpSender;


/**
 * This creates a new exporter to send spans to SignalFx. It includes a filter
 * to handle incoming requests that may contain spans.
 */
@Configuration
public class SfxTraceExporter {

    /**
     * This registers the filter for all url patterns
     */
    @Bean
    public FilterRegistrationBean tracingFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new TracingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    /**
     * Constructor that registers the new exporter
     *
     * @param ingestEndpoint the ingest url
     * @param serviceName the service name to export the spans with
     * @param accessToken the access token to authenticate with SignalFx
     */
    public SfxTraceExporter(
            @Value("${signalfx.ingest_url}") String ingestEndpoint,
            @Value("${signalfx.service_name}") String serviceName,
            @Value("${signalfx.access_token}") String accessToken) {
        // Create a new client with headers
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request()
                                   .newBuilder()
                                   .addHeader("X-SF-Token", accessToken)
                                   .build();

            return chain.proceed(request);
        }).build();

        // create a new sender using the client
        HttpSender sender = new HttpSender.Builder(ingestEndpoint)
                                          .withClient(client)
                                          .build();

        // create an exporter with our configured sender
        JaegerTraceExporter.createWithSender(sender, serviceName);
    }
}
