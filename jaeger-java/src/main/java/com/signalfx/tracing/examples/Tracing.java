package com.signalfx.tracing.examples;

import io.jaegertracing.Tracer;
import io.jaegertracing.reporters.RemoteReporter;
import io.jaegertracing.samplers.ConstSampler;
import io.jaegertracing.senders.zipkin.Zipkin2Sender;
import io.opentracing.util.GlobalTracer;
import okhttp3.Request;
import zipkin2.reporter.okhttp3.OkHttpSender;


public class Tracing {

    public static void initTracing(String accessToken) {
        OkHttpSender.Builder senderBuilder = OkHttpSender.newBuilder()
            .compressionEnabled(true)
            .endpoint("http://lab-ingest.corp.signalfuse.com/v1/trace");

        // Add an interceptor to inject the SignalFx X-SF-Token auth header.
        senderBuilder.clientBuilder().addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("X-SF-Token", accessToken)
                    .build();

            return chain.proceed(request);
        });

        OkHttpSender sender = senderBuilder.build();

        GlobalTracer.register(
            new Tracer.Builder("signalfx-jaeger-java-example")
                .withSampler(new ConstSampler(true))
                .withReporter(new RemoteReporter.Builder().withSender(Zipkin2Sender.create(sender)).build())
                .build());
    }
}
