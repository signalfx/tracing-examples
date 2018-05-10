# Zipkin Java (Brave) Example

This is a very simple Java class that shows the use of the [Zipkin Brave Java
tracer client](https://github.com/openzipkin/brave) with SignalFx. See
[./src/main/java/com/signalfx/tracing/examples/brave/App.java](./src/main/java/com/signalfx/tracing/examples/brave/App.java)
for the code and more information.

Brave's relationship to the OpenTracing API is [rather tenuous](https://github.com/opentracing/opentracing.io/issues/258)
so we don't recommend using the [OpenTracing adapter for Brave](https://github.com/openzipkin-contrib/brave-opentracing/)
at this time.  This example uses Brave directly.

## Building

To build this example, simply run `mvn package`.  This will create a jar that
you can run with:

`java -DaccessToken=MY_ORG_ACCESS_TOKEN` -jar ./target/brave-example-1.0-SNAPSHOT-shaded.jar

Provide your SignalFx organization's access token in the `accessToken` property.  This will run
the example app and send a single trace with the service name
`signalfx-zipkin-brave-example` with four spans.

If you want to see what errors look like, you can pass the property `-DthrowError=yes` and an exception
will occur within the parent span.

## Other Resources

The [README](https://github.com/openzipkin/brave/tree/master/brave) for the Brave artifact in the main
Brave repo is probably the best starting point for more information on using the tracer.

You can look at the various [sampler implementations](https://github.com/openzipkin/brave/tree/master/brave/src/main/java/brave/sampler)
that come included with Brave.

Zipkin Brave has [a good example
project](https://github.com/openzipkin/brave-webmvc-example) that goes into
more detail on how to use the tracer, as well as how to propagate span context across processes.
