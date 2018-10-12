# Zipkin, Spring-boot and SignalFx

# About

This example project demonstrates how to use Spring Boot, Zipkin, and SignalFx
together. This example uses the [Java Spring
Zipkin](https://github.com/opentracing-contrib/java-spring-zipkin) Project and
demonstrates how to configure the `Java Spring Zipkin` project to report traces
to SignalFx.

# References

- [Java Spring Zipkin](https://github.com/opentracing-contrib/java-spring-zipkin)
- [OpenTracing Java API](https://github.com/opentracing/opentracing-java)

# Configuration

The example project demonstrates the following modifications to a Spring
Application to send your trace spans to SignalFx. The following changes assume
you're already using the Spring Boot Web libraries.

## Required Configuration

### 1. Add the required dependencies

#### Maven

```xml
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-sender-okhttp3</artifactId>
</dependency>
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-spring-zipkin-web-starter</artifactId>
</dependency>
```

#### Gradle

```gradle
classpath 'io.zipkin.reporter2:zipkin-reporter'
classpath 'io.zipkin.reporter2:zipkin-sender-okhttp3'
classpath 'io.opentracing.contrib:opentracing-spring-zipkin-web-starter'
```

### 2. Configure a SignalFx OpenTracing Reporter

By providing a bean of type `ZipkinTracerCustomizer` in our `@Configuration`
class, we can explicitly set the `Reporter` on the `Tracing.Builder` object.
SignalFx requires that the access token be provided as a `X-SF-Token` request
header.  To accomplish this we redefine a `Reporter` that explicitly adds the
required header and then pass that into the `Tracer.Builder` via the
`ZipkinTracerCustomizer`.

#### Defining the SignalFx Reporter

```java
@Value("${opentracing.reporter.signalfx.ingest_url:https://ingest.signalfx.com/v1/trace}")
private String ingestUrl;

@Value("${opentracing.reporter.signalfx.access_token}")
private String accessToken;

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
```

#### Customize the Tracer to use the SignalFx Reporter

```java
@Bean
public ZipkinTracerCustomizer getCustomizerToAddSignalFxReporter() {
    ZipkinTracerCustomizer customizer = (Tracing.Builder builder) -> {
        builder.spanReporter(getSignalFxReporterInZipkinV2Format());
    };
    return customizer;
}
```

See [SignalFxZipkinReporterConfiguration.java](./src/main/java/com/signalfx/tracing/examples/SignalFxZipkinReporterConfiguration.java)
for the full source code.

### 3. Define Spring properties

`spring.application.name` is used as the service name and is picked up by core
OpenTracing libraries. `opentracing.reporter.signalfx.access_token` is required
to send data to SignalFx.

```ini
spring.application.name=Coin Flip
opentracing.reporter.signalfx.access_token=<<Access Token>>
```

### Configuration References

- [Full example application.properties](./src/main/resources/application.properties)
- [Jaeger Sampling Configuration Documentation](https://www.jaegertracing.io/docs/sampling/#client-sampling-configuration)
- [Spring Configuration Source Code](https://github.com/opentracing-contrib/java-spring-jaeger/blob/master/opentracing-spring-jaeger-starter/src/main/java/io/opentracing/contrib/java/spring/jaeger/starter/JaegerConfigurationProperties.java)

# Running the example project

Note: The example project uses [Maven](https://maven.apache.org) to build and
package the Spring Boot application.

## 1. Download/clone the project from the git repository

```
$ git clone https://github.com/signalfx/tracing-examples.git
$ cd tracing-examples/jaeger-java-spring-boot-web
```

## 2. Compile and package the Spring Boot Application

```
$ mvn package
```

## 3. Start your Spring Boot Application

```
$ java -jar target/coin-flip-service-with-zipkin-0.0.1-SNAPSHOT.jar
```

## 4. Make requests to the application to generate spans

Open <http://localhost:8080/flip> in your browser.

# Illustrated Concepts

## Defining a subspan

The example application sends spans to SignalFx for 100% of requests. Most of
the instrumentation is done by the `Zipkin Spring` library.  The [main
application](./src/main/java/com/signalfx/tracing/examples/Application.java#L41)
also wraps a function in a subspan called `calculateOdds`:

```java
private boolean trueWithProbability(double probability) {
    try (Scope scope = tracer.buildSpan("calculateOdds").startActive(true)) {
        return Math.random() <= probability;
    }
}
```

## Tagging the current Span

After the coin has been 'flipped', we tag the span so we can differentiate any
telemetry between the outcome of the coin flip.

```java
tracer.activeSpan().setTag("flipResult", flipResult);
```

See the [example code](./src/main/java/com/signalfx/tracing/examples/Application.java#L29)
for more context.
