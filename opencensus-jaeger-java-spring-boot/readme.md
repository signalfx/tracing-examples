# Opencensus Jaeger Exporter for Java

This example Spring Boot app uses the Opencensus Jaeger exporter to export
spans to SignalFx. There's an optional second app that shows how span contexts
can be sent across multiple apps.


# Setup and Usage

In order to authenticate with SignalFx, the `HttpSender` that sends spans must
be configured with a header that has the access token. The ability to configure
and set a `HttpSender` is not currently possible until the next exporter
release, `0.17.0`. However, it is currently available in a snapshot release.

To enable snapshot releases, add this to the list of profiles in
`~/.m2/settings.xml`:

```xml
<profile>
    <id>allow-snapshots</id>
        <activation><activeByDefault>true</activeByDefault></activation>
    <repositories>
        <repository>
            <id>snapshots-repo</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
            <releases><enabled>false</enabled></releases>
            <snapshots><enabled>true</enabled></snapshots>
        </repository>
    </repositories>
</profile>
```

Add these dependencies to the application:

```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-spring-jaeger-web-starter</artifactId>
    <version>0.2.1</version>
</dependency>
<dependency>
    <groupId>io.opencensus</groupId>
    <artifactId>opencensus-api</artifactId>
    <version>0.17.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>io.opencensus</groupId>
    <artifactId>opencensus-exporter-trace-jaeger</artifactId>
    <version>0.17.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>io.opencensus</groupId>
    <artifactId>opencensus-impl</artifactId>
    <version>0.17.0-SNAPSHOT</version>
    <scope>runtime</scope>
</dependency>
```

## Create and set the exporter

Add the following imports:

```java
import okhttp3.OkHttpClient;
import okhttp3.Request;
import com.uber.jaeger.senders.HttpSender;
```

Create an OkHttpClient instance with the necessary access token headers.

```java
OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
    Request request = chain.request()
                           .newBuilder()
                           .addHeader("X-SF-Token", accessToken)
                           .build();

    return chain.proceed(request);
}).build();
```

Build a sender with this client and register a trace exporter using
`createWithSender`, which accepts a sender and a service name.

```java
HttpSender sender = new HttpSender.Builder(ingestEndpoint)
                                  .withClient(client)
                                  .build();

JaegerTraceExporter.createWithSender(sender, serviceName);
```

Now the tracer can be retrieved from anywhere using `Tracing.getTracer()`.

## Using spans

The following imports are necessary for working with spans.

```java
import io.opencensus.common.Scope;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.propagation.TextFormat;
import io.opencensus.trace.samplers.Samplers;
import io.opencensus.trace.Span;
import io.opencensus.trace.SpanBuilder;
import io.opencensus.trace.Status;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
```

A span is built and started from the tracer.

```java
Span span = tracer.spanBuilder(name)
                  .setRecordEvents(true)
                  .setSampler(Samplers.alwaysSample())
                  .startSpan();
                  
doSomeWork();

span.end();
```

To start a new child span:

```java
try (Scope ws = tracer.withSpan(span)) {
    // child span start
    ...
}
```

## Propagate spans across HTTP

When making a HTTP request, the current span can be injected to ensure that the
remote process can create spans under the right context.

Create a `TextFormat` and inject the span for propagation:

```java
TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();
TextFormat.Setter setter = new TextFormat.Setter<HttpURLConnection>() {
    public void put(HttpURLConnection carrier, String key, String value) {
        carrier.setRequestProperty(key, value);
    }
};

HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
textFormat.inject(span.getContext(), conn, setter);
conn.setRequestMethod(HttpMethod.GET.name());
```

The request endpoint can then extract the span.

```java
TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();
TextFormat.Getter getter = new TextFormat.Getter<HttpServletRequest>() {
    public String put(HttpServletRequest request, String key) {
        return request.getHeader(key);
    }
};

SpanContext spanContext = textFormat.extract(request, getter);
```

A new child span can be built with this `SpanContext`.

```java
Span span = tracer.spanBuilderWithRemoteParent(spanName, spanContext)
                  .setRecordEvents(true)
                  .setSampler(Samplers.alwaysSample())
                  .startSpan();
```


# Running the example

In the example-app directory, build and run the package.

```bash
mvn package
java -jar target/opencensus-with-jaeger-0.0.1-SNAPSHOT.jar
```

This will start the app listening on port 8098. Going to
http://localhost:8098/test in a browser will create the traces and send them to
SignalFx. However, unless a second app, `remote-app`, is running, there will be
no text to display, and the spans will be tagged with an error message.

The second app is in the `remote-app` folder.

```bash
mvn package
java -jar target/remote-opencensus-with-jaeger-0.0.1-SNAPSHOT.jar
```

This will start another app at port 8099 which will keep track of the number of
requests. It can be accessed directly at http://localhost:8099/count, or through
the first app which will make a request to this endpoint.

Access tokens will need to be added to each app's pom.xml.


# References

For more information about the tracer:
- https://github.com/census-instrumentation/opencensus-java

This example follows the post here:
- https://medium.com/@mauro.canuto88/distributed-tracing-with-opencensus-and-jaeger-in-java-7de6454b0aa0
