# Spring Boot Auto-Instrumentation Example

This is an example of automatically producing traces using the
[Splunk distribution of OpenTelemetry Java instrumentation project](https://github.com/signalfx/splunk-otel-java).  
This example is of a simple wishlist web app that is auto-instrumented by providing
the agent jar via the required JVM command-line option.

## Building the example app

To run this example locally and send traces to your available Smart Agent or Gateway,
please clone this repository and from this directory do the following:

```bash
$ # docker-compose is required for postgres instance
$ pip install docker-compose
$ # download the newest version of the agent
$ sudo curl -sSL -o /opt/opentelemetry-javaagent-all.jar 'https://github.com/signalfx/signalfx-otel-java/releases/latest/download/signalfx-otel-javaagent-all.jar'
$ ./start.sh
```

Here are some environment variables you might need to set to configure the Java
agent:
```
$ # Exporter configuration - default is Zipkin exporter with endpoint URL http://localhost:9080/v1/trace"
$ # Eg. set OTLP exporter - compatible with SignalFX SmartAgent
$ export OTEL_EXPORTER=otlp

$ # Exporter endpoint URL - each exporter has own, specific property name and the default value  
$ # Eg. set OTLP exporter URL - default is localhost:55680
$ export OTEL_OTLP_ENDPOINT=localhost:9411

$ # Exporter service name - each exporter has own, specific property name
$ # Eg. set ZIPKIN service name
$ export OTEL_ZIPKIN_SERVICE_NAME=my-java-app
```

## Accessing the Wishlist

The example application is a simple wishlist that allows adding desired items for
individual users.  By default, you can access the site via http://localhost:8080/ in your
web browser.
