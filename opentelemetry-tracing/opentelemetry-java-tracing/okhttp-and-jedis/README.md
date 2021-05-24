# Java Agent

This example shows the use of the [Splunk distribution of OpenTelemetry Java project](https://github.com/signalfx/splunk-otel-java).  
The Java agent performs bytecode manipulation of the target application using the [JVM
Instrumentation](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html)
interface and enables tracing of many popular libraries and frameworks without
requiring target application's developer to modify its source code.

## Example application
This example consists of simple command line application written in Java.
When this application starts, it makes a http call to a remote server and then
stores the result of that call into Redis database.
This will produce a trace that is exported to the locally running [OpenTelemetry Collector](https://opentelemetry.io/docs/collector/getting-started/).

## Getting started
To run the example just execute
```shell
./start.sh
```

You then can see the produced telemetry in the standard output of the Collector docker container:

```shell
docker logs -f collector
```

In the end please stop and remove Collector container by running
```shell
docker stop collector
```

## More information
Please read comments in [start.sh](./start.sh) script to understand all the steps involved in this example.
In order to see how telemetry is produced and how the application can communicate with
instrumentation agent, please read [the source code](./src/main/java/com/splunk/tracing/otel/examples/javaagent/App.java).