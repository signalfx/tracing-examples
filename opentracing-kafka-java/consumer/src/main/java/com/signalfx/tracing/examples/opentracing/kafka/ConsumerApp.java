package com.signalfx.tracing.examples.opentracing.kafka;

public class ConsumerApp {

    public static void main(String[] args) {
        String ingestUrl = System.getProperty("ingestUrl", "https://ingest.signalfx.com");
        String accessToken = System.getProperty("accessToken");
        String kafkaBrokers = System.getProperty("kafkaBrokers");
        String kafkaTopic = System.getProperty("kafkaTopic");

        System.out.printf("Consumer: url=%s token=%s brokers=%s topic=%s%n",
                ingestUrl, accessToken, kafkaBrokers, kafkaTopic);
    }
}
