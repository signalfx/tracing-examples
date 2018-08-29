package example;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.spi.Reporter;
import io.jaegertracing.thrift.internal.senders.HttpSender;
import io.opentracing.contrib.java.spring.jaeger.starter.ReporterAppender;
import jaeger.okhttp3.OkHttpClient;
import jaeger.okhttp3.Request;

@Configuration
public class SignalFxJaegerReporterConfiguration {
	
	@Value("${opentracing.reporter.signalfx.ingest_url:https://ingest.signalfx.com/v1/trace}")
	private String ingestUrl;
	
	@Value("${opentracing.reporter.signalfx.access_token}")
	private String accessToken;
	
	/**
	 * Add the SignalFx reporter to the list of destinations for traces.
	 */
	@Bean 
	public ReporterAppender getSignalFxReporterAppender() {
		return (Collection<Reporter> reporters) -> {
			reporters.add(createSignalFxReporter());
		};
	}
	
	private Reporter createSignalFxReporter() {
		// Setup the HttpSender to report to SignalFx with the access token
		OkHttpClient signalFxHttpClientWithAuthHeaders = new OkHttpClient.Builder().addInterceptor(chain -> {
		    Request request = chain.request().newBuilder()
		            .addHeader("X-SF-Token", accessToken)
		            .build();
		    return chain.proceed(request);
		}).build();
		
		HttpSender.Builder senderBuilder = new HttpSender.Builder(ingestUrl)
			.withClient(signalFxHttpClientWithAuthHeaders);

		return new RemoteReporter.Builder()
			.withSender(senderBuilder.build())
			.build();
	}
}