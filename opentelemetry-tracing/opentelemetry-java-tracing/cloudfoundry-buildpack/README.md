# CloudFoundry deployment with Java buildpack

This is an example of automatically producing traces using the
[Splunk distribution of OpenTelemetry Java instrumentation agent](https://github.com/signalfx/splunk-otel-java).
This example is a simple web app that just executes an HTTP call when its only endpoint is called.
It's auto-instrumented by the CloudFoundry Java buildpack that downloads the agent jar and configures it via JVM options.

## Building the example app

To run this example locally and send traces to your available Smart Agent or OpenTelemetry Collector,
please clone this repository and from this directory do the following:

```sh
$ ./mvnw clean install
```

To verify that it works you can just start the jar and check if the endpoint responds:

```sh
$ # in first console window
$ java -jar target/cloudfoundry-buildpack-demo-0.0.1-SNAPSHOT.jar
$ # in second console window
$ curl http://localhost:8080/get/example
```

## Deploying to CloudFoundry

Please install the Splunk distribution of OpenTelemetry Instrumentation Buildpack using [these instructions](https://github.com/signalfx/splunk-otel-java/tree/master/deployments/cloudfoundry/buildpack/README.md).
In case you run `cf create-buildpack` manually please make sure the buildpack name remains unchanged - or please change the `manifest.yml` file.

Now you can deploy this example to your CloudFoundry org:

```sh
# Configure the tracing library
$ cf set-env my-app OTEL_ZIPKIN_SERVICE_NAME <application name>

# Deploy the app using manifest.yml
$ cf push -p target/cloudfoundry-buildpack-demo-0.0.1-SNAPSHOT.jar
```

The Splunk distribution of OpenTelemetry Java Instrumentation agent will be automatically added to your app by the buildpack.
