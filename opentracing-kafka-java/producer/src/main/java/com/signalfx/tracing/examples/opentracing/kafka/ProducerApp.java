package com.signalfx.tracing.examples.opentracing.kafka;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.sampler.CountingSampler;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import okhttp3.Request;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class ProducerApp {

    private static final String NAME = "signalfx-opentracing-kafka-java-producer-example";

    public static void main(String[] args) throws Exception {
        // Here we instantiate our TracingHelper class and get the tracer from it.  Normally this would
        // be done by your DI framework and the resulting tracer injected to each class that needs it.
        TracingHelper tracingHelper = new TracingHelper();
        Tracer tracer = tracingHelper.getTracer();

        Producer<Long, String> producer = createKafkaProducer(tracer);

        String kafkaTopic = System.getProperty("kafkaTopic");
        CountDownLatch latch = new CountDownLatch(1);

        try (Scope scope = tracer.buildSpan("producer.say_hi").startActive(true)) {
            System.out.printf("Sending message on Kafka topic %s...%n", kafkaTopic);
            producer.send(new ProducerRecord<>(kafkaTopic, 42L, "Hello, world!"), (r, e) -> {
                if (e != null) {
                    System.err.printf("Failed to send Kafka message on %s: %s%n", kafkaTopic, e
                            .getMessage());
                } else {
                    System.out.printf("Sent Kafka message on %s.%n", kafkaTopic);
                }
                latch.countDown();
            });
        }

        latch.await();
        tracingHelper.close();
        System.out.println("Done.");
    }

    /**
     * Create a Kafka producer that is configured to send to the brokers specified in the
     * kafkaBrokers system property.
     */
    private static Producer<Long, String> createKafkaProducer(Tracer tracer) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("kafkaBrokers"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, NAME);
        return new TracingKafkaProducer<>(new KafkaProducer<>(properties), tracer);
    }

    /**
     * A helper class that encapsulates all of the Brave objects that need to be created and cleaned up
     * upon shutdown.  If you are using a DI framework, this logic would be used by that
     * to create a single instance of the tracer and inject it to every class that needs it.  Be sure
     * to incorporate the logic in the close method to your DI framework's shutdown logic.
     */
    private static class TracingHelper implements Closeable {

        // We need to keep references to all of these components because they have to be closed upon
        // shutdown in a certain order to avoid losing spans.
        private AsyncReporter<zipkin2.Span> reporter;
        private Tracer tracer;
        private OkHttpSender sender;

        TracingHelper() {
            // The ingest url is where the span data will be sent, which can normally just be the default
            // value of this property.
            String ingestUrl = System.getProperty("ingestUrl", "https://ingest.signalfx.com");
            // This would be your organization's SignalFx access token, accessed in whatever manner most
            // appropriate to your environment.
            String accessToken = System.getProperty("accessToken");

            // Build the sender that does the HTTP request containing spans to our ingest server.
            OkHttpSender.Builder senderBuilder = OkHttpSender.newBuilder()
                    .compressionEnabled(true)
                    .endpoint(ingestUrl + "/v1/trace");

            // Add an interceptor to inject the SignalFx X-SF-Token auth header.
            senderBuilder.clientBuilder().addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("X-SF-Token", accessToken)
                        .build();
                return chain.proceed(request);
            });

            this.sender = senderBuilder.build();
            this.reporter = AsyncReporter.create(sender);

            // Create the Tracing instance from which we obtain the tracer instance
            this.tracer = BraveTracer.create(
                    Tracing.newBuilder()
                    // This sets the name of the local application and will be fairly prominent in the Zipkin UI.
                    .localServiceName(NAME)
                    .spanReporter(reporter)
                    // Use a sampler that always reports spans.  You can swap this out for other samplers.
                    .sampler(CountingSampler.create(1.0f))
                    .build());
        }

        /**
         * Return the tracer instance from the Tracing object.  This is what spans are created through.
         */
        public Tracer getTracer() {
            return tracer;
        }

        /**
         * This might be part of the shutdown logic if using a DI framework.  It should be called one way
         * or another though.
         */
        @Override
        public void close() {
            reporter.flush();
            reporter.close();
            sender.close();
        }
    }
}
