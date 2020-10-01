# CloudFoundry deployment with SignalFx Tracing Library for .NET

This is an example of automatically producing traces using the
[SignalFx Tracing Library for .NET](https://github.com/signalfx/signalfx-dotnet-tracing/).
This example is a simple web app that just executes an HTTP call when its only endpoint is called.
It's auto-instrumented by the SignalFx Tracing Library for .NET Buildpack.

## Building the example app

To run this example locally and send traces to your available Smart Agent or OpenTelemetry Collector,
please clone this repository and from this directory do the following:

```sh
$ cake
```

To verify that it works you can just run it and check if the endpoint responds:

```sh
$ # in first console window
$ dotnet run
$ # in second console window
$ curl http://localhost:8080/get/example
```

## Deploying to CloudFoundry

Please install the SignalFx Tracing Library for .NET Buildpack using [these instructions](https://github.com/signalfx/signalfx-dotnet-tracing/tree/master/deployments/cloudfoundry/buildpack/README.md).
In case you run `cf create-buildpack` manually please make sure that the buildpack name remains unchanged - or please change the `manifest.yml` file.

Now you can deploy this example to your CloudFoundry org:

```sh
# Configure the tracing library
$ cf set-env my-app SIGNALFX_SERVICE_NAME <application name>
$ cf set-env my-app SIGNALFX_ENDPOINT_URL <Smart agent or OTel collector address>

# Deploy the app using manifest.yml
$ cf push
```

The SignalFx Tracing Library for .NET will be automatically applied to your app by the buildpack.
