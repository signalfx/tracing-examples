package com.signalfx.tracing.examples;

import io.opencensus.exporter.trace.jaeger.JaegerTraceExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import com.uber.jaeger.senders.HttpSender;


/**
 * This creates and registers the exporter used to send traces
 */
@Configuration
public class SfxTraceExporter {

    /**
     * A new Sender is created with headers to authenticate with SignalFx. The
     * sender is then used to register a new tracer exporter.
     *
     * @param ingestEndpoint the ingest url
     * @param serviceName the name to send spans as
     * @param accessToken the access token for SignalFx
     */
    public SfxTraceExporter(
            @Value("${signalfx.ingest_url}") String ingestEndpoint,
            @Value("${signalfx.service_name}") String serviceName,
            @Value("${signalfx.access_token}") String accessToken) {
        // Create an OkHttpClient with headers
        OkHttpClient clientWithAuth = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request()
                                   .newBuilder()
                                   .addHeader("X-SF-Token", accessToken)
                                   .build();

            return chain.proceed(request);
        }).build();

        // use the client when instantiating the HttpSender
        HttpSender sender = new HttpSender.Builder(ingestEndpoint)
                                          .withClient(clientWithAuth)
                                          .build();

        // create an exporter with our configured sender
        JaegerTraceExporter.createWithSender(sender, serviceName);
    }
}
