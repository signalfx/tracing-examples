package com.signalfx.tracing.examples;

import io.opencensus.exporter.trace.jaeger.JaegerTraceExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import com.uber.jaeger.senders.HttpSender;

@Configuration
public class SfxTraceExporter {

    public SfxTraceExporter(
            @Value("${signalfx.ingest_url}") String ingestEndpoint,
            @Value("${signalfx.service_name}") String serviceName,
            @Value("${signalfx.access_token}") String accessToken) {

        OkHttpClient clientWithAuth = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request()
                                   .newBuilder()
                                   .addHeader("X-SF-Token", accessToken)
                                   .build();

            return chain.proceed(request);
        }).build();

        HttpSender sender = new HttpSender.Builder(ingestEndpoint)
                                          .withClient(clientWithAuth)
                                          .build();

        JaegerTraceExporter.createWithSender(sender, serviceName);
    }
}
