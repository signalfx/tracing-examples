package com.signalfx.tracing.examples.opentracing.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import io.opentracing.util.GlobalTracer;
import okhttp3.Request;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class ProducerApp {

    private static final String NAME = "signalfx-opentracing-kafka-java-producer-example";

    public static void main(String[] args) {
        // Create a single instance of a Jaeger tracer that will be used throughout the application.
        // If you are using a DI framework, you should rely on that as much as possible to provide
        // this instance.  Here we are defining the tracer as an OpenTracing tracer, since it implements
        // that interface.  You should generally use the OpenTracing interface where possible to make
        // it potentially easier to swap out tracers in the future.
        Tracer tracer = createTracer();

        Producer<Long, String> producer = createKafkaProducer(tracer);

        String kafkaTopic = System.getProperty("kafkaTopic");

        try (Scope scope = tracer.buildSpan("root").startActive(true)) {
            System.out.printf("Sending message on Kafka topic %s...%n", kafkaTopic);
            producer.send(new ProducerRecord<>(kafkaTopic, 42L, "Hello, world!"), (r, e) -> {
                if (e != null) {
                    System.err.printf("Failed to send Kafka message on %s: %s%n", kafkaTopic, e
                            .getMessage());
                } else {
                    System.out.printf("Sent Kafka message on %s.%n", kafkaTopic);
                }
            });
        }
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
     * Create a Jaeger tracer instance that is configured to send span data to SignalFx.  This is
     * intended to be called once.  If you are using a DI framework, this logic would be used by
     * that to create a single instance of the tracer and inject it to every class that needs it.
     */
    private static Tracer createTracer() {
        String ingestUrl = System.getProperty("ingestUrl", "https://ingest.signalfx.com");
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

        OkHttpSender sender = senderBuilder.build();

        // Build the Jaeger Tracer instance, which implements the opentracing Tracer interface.
        io.opentracing.Tracer tracer = new io.jaegertracing.Tracer.Builder(NAME)
                // This configures the tracer to send all spans, but you will probably want to use
                // something less verbose.
                .withSampler(new ConstSampler(true))
                // Configure the tracer to send spans in the Zipkin V2 JSON format instead of the
                // default Jaeger protocol, which we do not support.
                .withReporter(new Zipkin2Reporter(AsyncReporter.create(sender)))
                .build();

        // It is considered best practice to at least register the GlobalTracer instance, even if you
        // don't generally use it.
        GlobalTracer.register(tracer);

        return tracer;
    }
}
