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

Please set following env properties (startup scripts sets these as well):
```
$ # This is the ZIPKIN exporter - compatible with SignalFX SmartAgent
$ export  OTEL_EXPORTER=zipkin

$ # This is the default endpoint url
$ export  OTEL_ZIPKIN_ENDPOINT=http://localhost:9080/v1/trace

$ # Replace this with your application's common name
$ export OTEL_ZIPKIN_SERVICE_NAME=unnamed-java-app
```

## Accessing the Wishlist

The example application is a simple wishlist that allows adding desired items for
individual users.  By default, you can access the site via http://localhost:8080/ in your
web browser.
