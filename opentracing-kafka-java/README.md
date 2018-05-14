# OpenTracing Kafka tracing example (Java)

This example demonstrates how to trace asynchronous or pub/sub-style
communication via Kafka using OpenTracing client libraries and SignalFx. It
contains an example producer application and an example consumer application
that are instrumented and report to SignalFx.

## Building

To build this example, simply run `mvn package`.  This will create a JAR for each
application. Run the producer with:

```
$ java -DaccessToken=MY_ORG_ACCESS_TOKEN -DkafkaBrokers=BROKER_LIST -DkafkaTopic=TOPIC_NAME \
       -jar ./producer/target/opentracing-kafka-java-producer-example-1.0-SNAPSHOT-shaded.jar
```

You can then run the consumer to consume the message produced:

```
$ java -DaccessToken=MY_ORG_ACCESS_TOKEN -DkafkaBrokers=BROKER_LIST -DkafkaTopic=TOPIC_NAME \
       -jar ./consumer/target/opentracing-kafka-java-consumer-example-1.0-SNAPSHOT-shaded.jar &
```

In both cases, provide your SignalFx organization's access token in the
`accessToken` property, a comma-separated list of Kafka brokers in the
`kafkaBrokers` property, and a topic name to use in the `kafkaTopic` property.
This will run the example producer and the example consumer, exchanging a
message over the defined Kafka topic and reporting a single trace encompassing
service `signalfx-opentracing-kafka-java-producer-example` and
`signalfx-opentracing-kafka-java-consumer-example`.

## Other Resources

The OpenTracing client library providing the integration with the Kafka producer
and Kafka consumer:

https://github.com/opentracing-contrib/java-kafka-client
