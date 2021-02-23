# Java Agent

This example shows the use of the [Splunk distribution of OpenTelemetry Java instrumentation project](https://github.com/signalfx/splunk-otel-java).  
The Java agent performs bytecode manipulation of the target application using the [JVM
Instrumentation](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html)
interface and enables tracing of many popular libraries and frameworks without
modification of the target application's source code.

In order to run this example, you will need to run the opentelemetry collector locally and have it configured to 
receive otlp spans and export data to Splunk's ingest endpoints. 

TODO: provide a link to how to do set up the collector for this demo. Or, better yet, make it more self-contained.

Here are some environment variables you might need to set to configure the Java
agent:
```
$ # Exporter configuration - default is a Jaeger exporter with endpoint URL http://localhost:14268/api/traces"
$ # Eg. set OTLP exporter - compatible with SignalFX SmartAgent
$ export OTEL_TRACES_EXPORTER=otlp

$ # Exporter service name - each exporter has own, specific property name
$ # Eg. set ZIPKIN service name
$ export OTEL_RESOURCE_ATTRIBUTES=service.name=my-java-app
```

To obtain the latest Java agent, download it to your host's filesystem:

```
curl -sSL -o opentelemetry-javaagent-all.jar 'https://github.com/signalfx/splunk-otel-java/releases/latest/download/splunk-otel-javaagent-all.jar'
```

Make sure it is not writable by unsecured users since modifications to the
agent could easily compromise your application. Then simply add the flag
`-javaagent:opentelemetry-javaagent-all.jar` to your Java invocation and run your
application otherwise as normal.

You can run this particular example by first packaging the dependencies with
Maven and the running it with `-javaagent:`:

```sh
$ # Export the environment variables in this shell as shown above for your org
$ mvn package

$ # Run a Redis server locally that the example app will use
$ docker run -d --name redis-tracing-test -p 6379:6379 redis

$ java -javaagent:opentelemetry-javaagent-all.jar -jar target/java-agent-example-1.0-SNAPSHOT-shaded.jar https://google.com
```
There is also `start.sh` script provided for convenience, setting exemplary environmental variables, running redis and the application.