package com.signalfx.tracing.examples;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import brave.Tracing;
import io.opentracing.contrib.java.spring.zipkin.starter.ZipkinTracerCustomizer;
import okhttp3.Request;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
@Import(io.opentracing.contrib.java.spring.zipkin.starter.ZipkinAutoConfiguration.class)
public class SignalFxZipkinReporterConfiguration {

    @Value("${opentracing.reporter.signalfx.ingest_url:https://ingest.signalfx.com/v1/trace}")
    private String ingestUrl;

    @Value("${opentracing.reporter.signalfx.access_token}")
    private String accessToken;

    @Bean
    public ZipkinTracerCustomizer getCustomizerToAddSignalFxReporter() {
	ZipkinTracerCustomizer customizer = (Tracing.Builder builder) -> {
	    builder.spanReporter(getSignalFxReporterInZipkinV2Format());
	};
	return customizer;
    }

    private Reporter<Span> getSignalFxReporterInZipkinV2Format() {
	return AsyncReporter.create(getSignalFxHttpSender());
    }

    private Sender getSignalFxHttpSender() {

	// Setup the HttpSender to report to SignalFx with the access token
	OkHttpSender.Builder senderBuilder = OkHttpSender.newBuilder()
		.compressionEnabled(true)
		.endpoint(ingestUrl);

	senderBuilder.clientBuilder().addInterceptor(chain -> {
	    Request request = chain.request().newBuilder()
		    .addHeader("X-SF-Token", accessToken)
		    .build();
	    return chain.proceed(request);
	});

	return senderBuilder.build();
    }

}