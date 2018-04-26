# Jaeger Java Example

This is a very simple Java class that shows the use of the Jaeger Java tracer client with SignalFx.
See [./src/main/java/com/signalfx/tracing/examples/App.java](./src/main/java/com/signalfx/tracing/examples/App.java)
for the code and more information.

## Building

To build this example, simply run `mvn package`.  This will create a jar that
you can run with:

`java -jar ./target/jaeger-example-1.0-SNAPSHOT-shaded.jar -DaccessToken=MY_ORG_ACCESS_TOKEN`

Provide your SignalFx organization's access token in the `accessToken` property.  This will run
the example app and send a single trace with the service name
`signalfx-jaeger-java-example` with four spans.

## Other Resources

Here is a good tutorial on general use (not SignalFx-specific) of the Jaeger Java implementation:

https://github.com/yurishkuro/opentracing-tutorial/tree/master/java
