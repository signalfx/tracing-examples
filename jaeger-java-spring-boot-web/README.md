# Jaeger, Spring-boot and SignalFx

# About

This example project demonstrates how to use Spring Boot, Jaeger, and SignalFx 
together. This example uses the [Java Spring Jaeger](https://github.com/opentracing-contrib/java-spring-jaeger)
Project and demonstrates how to configure the `Java Spring Jaeger` project to 
report `Spans` to SignalFx.

# References

- [Java Spring Jaeger](https://github.com/opentracing-contrib/java-spring-jaeger)
- [OpenTracing Java API](https://github.com/opentracing/opentracing-java)

# Configuration

The example project demonstrates the following modifications to a Spring 
Application to send your `Spans` to SignalFx. The following changes assume 
you're already using the Spring Boot Web libraries.

## Required Configuration

### 1. Add the required dependencies

#### Maven

```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-spring-jaeger-web-starter</artifactId>
</dependency>
```

#### Gradle

```gradle
classpath 'io.opentracing.contrib:opentracing-spring-jaeger-web-starter'
```

### 2. Configure a SignalFx OpenTracing Reporter

By providing a bean of type `ReporterAppender` in our `@Configuration` class, we 
can  add a custom reporter to the list of `Reporters` used by Jaeger. The Jaeger 
Spring library allows for HTTP output but SignalFx requires that the 
`Access Token` be provided in the request header under `X-SF-Token`.  To 
accomplish this we redefine a `Reporter` that explicitly adds the required 
header.  

#### Defining the SignalFx Reporter

```java    @Value("${opentracing.reporter.signalfx.ingest_url:https://ingest.signalfx.com/v1/trace}")
private String ingestUrl;
    
@Value("${opentracing.reporter.signalfx.access_token}")
private String accessToken;
    
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
```

#### Appending the SignalFx Reporter

```java
@Bean 
public ReporterAppender getSignalFxReporterAppender() {
    return (Collection<Reporter> reporters) -> {
        reporters.add(createSignalFxReporter());
    };
}
```
See [SignalFxJaegerReporterConfiguration.java](https://github.com/signalfx/tracing-examples/tree/spring-boot-examples/jaeger-java-spring-boot-web/src/main/java/com/signalfx/tracing/examples/SignalFxJaegerReporterConfiguration.java) for the full source code.

### 3. Define Spring properties

`spring.application.name` is used as the `Service Name` and is picked up by core 
OpenTracing libraries. `opentracing.reporter.signalfx.access_token` is required 
to send data to SignalFx.
```ini
spring.application.name=Coin Flip
opentracing.reporter.signalfx.access_token=<<Access Token>>
```

## Optional Configuration

### Sampling 

By default the Jaeger library will sample 100% of requests if no other sampling 
strategies are defined, which works well for our example application. If this 
isn't desireable define a strategy by using the [Jaeger Spring configuration options](https://github.com/opentracing-contrib/java-spring-jaeger/blob/master/README.md#configuration-options).
#### Configuration References

- [Full example application.properties](https://github.com/signalfx/tracing-examples/tree/spring-boot-examples/jaeger-java-spring-boot-web/src/main/resources/application.properties)
- [Jaeger Sampling Configuration Documentation](https://www.jaegertracing.io/docs/sampling/#client-sampling-configuration)
- [Spring Configuration Source Code](https://github.com/opentracing-contrib/java-spring-jaeger/blob/master/opentracing-spring-jaeger-starter/src/main/java/io/opentracing/contrib/java/spring/jaeger/starter/JaegerConfigurationProperties.java)

# Running the example project

Note: The example project uses [Maven](https://maven.apache.org) to build and 
package the Spring Boot application. 
## 1. Download/clone the project from the git repository
```bash
git clone https://github.com/signalfx/tracing-examples.git
cd tracing-examples/jaeger-java-spring-boot-web
```

## 2. Compile and package the Spring Boot Application

```bash
$ mvn package
```

## 3. Start your Spring Boot Application

```bash
$ java -jar target/coin-flip-service-with-jaeger-0.0.1-SNAPSHOT.jar
```

## 4. Make requests to the application to generate `Spans` 

Open <http://localhost:8080/flip> in your browser. 

# Illustrated Concepts

## Defining a subspan

The example application sends `Span`s to SignalFx for 100% of requests. Most of 
the instrumentation is done by the `Jaeger Spring` library.  The [main application](https://github.com/signalfx/tracing-examples/tree/spring-boot-examples/jaeger-java-spring-boot-web/src/main/java/com/signalfx/tracing/examples/Application.java#L39) 
also wraps a function in a subspan called `calculateOdds`:  
```java
private boolean returnFalseWithProbability(Double probability) {
    try (Scope scope = tracer.buildSpan("calculateOdds").startActive(true)) {
        Double greaterThanValue = (100-probability)/100;
        return Math.random() > greaterThanValue;
    } 
}
```

## Tagging the current Span

After the coin has been 'flipped', we tag the `Span` so we can differentiate 
any telemetry between the outcome of the coin flip.  
```java
tracer.activeSpan().setTag("flipResult", flipResult);
```
See the [example code](https://github.com/signalfx/tracing-examples/tree/spring-boot-examples/jaeger-java-spring-boot-web/src/main/java/com/signalfx/tracing/examples/Application.java#L29) 
for more context.
