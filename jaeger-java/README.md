# Jaeger Java Example

This is a very simple Java class that shows the use of the Jaeger Java tracer
client with SignalFx. See
[./src/main/java/com/signalfx/tracing/examples/jaeger/App.java](./src/main/java/com/signalfx/tracing/examples/jaeger/App.java)
for the code and more information.

## Building

To build this example, simply run `mvn package`. This will create a Jar that you
can run with:

```
java -DaccessToken=MY_ORG_ACCESS_TOKEN -jar ./target/jaeger-example-1.0-SNAPSHOT-shaded.jar
```

Provide your SignalFx organization's access token in the `accessToken` property.
This will run the example app and send a single trace with the service name
`signalfx-jaeger-java-example` with four spans.

## Other Resources

 - [A good tutorial on general use of the Jaeger Java tracer](https://github.com/yurishkuro/opentracing-tutorial/tree/master/java)
 - [Example of using Jaeger with Spring
   Boot](http://www.hawkular.org/blog/2017/06/9/opentracing-spring-boot.html#_jaeger):
   There is a demo repo linked at the bottom of the article as well
