package com.signalfx.tracing.examples;

import io.opencensus.exporter.trace.jaeger.JaegerTraceExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import com.uber.jaeger.senders.HttpSender;


@Configuration
public class SfxTraceExporter {

    @Bean
    public FilterRegistrationBean tracingFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new TracingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    
    public SfxTraceExporter(
            @Value("${signalfx.ingest_url}") String ingestEndpoint,
            @Value("${signalfx.service_name}") String serviceName,
            @Value("${signalfx.access_token}") String accessToken) {

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request()
                                   .newBuilder()
                                   .addHeader("X-SF-Token", accessToken)
                                   .build();

            return chain.proceed(request);
        }).build();

        HttpSender sender = new HttpSender.Builder(ingestEndpoint)
                                          .withClient(client)
                                          .build();

        JaegerTraceExporter.createWithSender(sender, serviceName);
    }
}
