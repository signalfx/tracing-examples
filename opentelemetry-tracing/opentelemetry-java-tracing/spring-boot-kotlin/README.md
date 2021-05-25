# Spring Boot Auto-Instrumentation Example

This is an example of automatically producing traces using the
[Splunk distribution of OpenTelemetry Java instrumentation project](https://github.com/signalfx/splunk-otel-java).  
This is a simple wishlist web app that is automatically instrumented by
the instrumentation agent which is attached to the JVM via command-line option.

## Example application

The example application is a simple wishlist that allows adding desired items for individual users.
When you interact with the application via a web browser OpenTelemetry instrumentation will produce telemetry
and send it to locally running [OpenTelemetry Collector](https://opentelemetry.io/docs/collector/getting-started/).

## Getting started

To run the example just execute
```shell
./start.sh
```

This will build and run the demo application together with Collector. 
Point your web browser to [http://localhost:8080](http://localhost:8080) and interact with the application.
You can see the produced telemetry in the standard output of the Collector docker container:

```shell
docker logs -f collector
```

You can stop the application by pressing CTRL-C.

## More information
Please read comments in [start.sh](./start.sh) script to understand all the steps involved in this example.
In order to see how telemetry is produced and how the application can communicate with
instrumentation agent, please read [application source code](./src/main/kotlin).