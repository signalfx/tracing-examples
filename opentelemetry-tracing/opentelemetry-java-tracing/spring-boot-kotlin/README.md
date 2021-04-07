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
$ curl -sSL -o splunk-otel-javaagent-all.jar 'https://github.com/signalfx/signalfx-otel-java/releases/latest/download/splunk-otel-javaagent-all.jar'
$ ./start.sh
```

Here are some environment variables you might need to set to configure the Java
agent:
```
$ # Exporter configuration - default is the Jaeger Thrift exporter with endpoint URL "http://localhost:9080/v1/trace"
$ # Eg. to set up the application to use the OTLP exporter - compatible with SignalFX SmartAgent
$ export OTEL_TRACES_EXPORTER=otlp

$ # Exporter endpoint URL - each exporter has own, specific property name and the default value  
$ # Eg. to set up the OTLP exporter URL - default is http://localhost:4317
$ export OTEL_OTLP_ENDPOINT=http://localhost:12345

$ # Service name - Use resource attributes to set this.
$ export OTEL_RESOURCE_ATTRIBUTES=service.name=my-java-app
```

## Accessing the Wishlist

The example application is a simple wishlist that allows adding desired items for
individual users.  By default, you can access the site via http://localhost:8080/ in your
web browser.
