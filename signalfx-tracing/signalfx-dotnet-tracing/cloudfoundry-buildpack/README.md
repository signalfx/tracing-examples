# CloudFoundry deployment with SignalFx Tracing Library for .NET

This is an example of automatically producing traces using the
[SignalFx Tracing Library for .NET](https://github.com/signalfx/signalfx-dotnet-tracing/).
This example is a simple web app that just executes an HTTP call when its only endpoint is called.
It's auto-instrumented by the SignalFx Tracing Library for .NET Buildpack.

## Building the example app

To deploy this example on CloudFoundry you need to have [cfcli](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html) installed.
If you want to compile and run it locally .NET Core needs to be installed on your machine.

### Targetting Linux stack

To run this example locally and send traces to your available Smart Agent or OpenTelemetry Collector,
please run the following command from this directory:

```sh
$ dotnet publish
```

To verify that it works you can just run it and check if the endpoint responds:

```sh
$ # in first console window
$ dotnet run
$ # in second console window
$ curl http://localhost:8080/get/example
```

### Targetting Windows stack

To build a Windows executable please run the following command from this directory:

```sh
$ dotnet publish -o publish -r win-x64
```

This will create a `publish` directory with all compiled binaries.

## Deploying to CloudFoundry

Please install the SignalFx Tracing Library for .NET Buildpack using [these instructions](https://github.com/signalfx/signalfx-dotnet-tracing/tree/master/deployments/cloudfoundry/buildpack/README.md).
In case you run `cf create-buildpack` manually please make sure that the buildpack name remains unchanged - or please change the `manifest.yml` file.

### Deploying to a Linux stack

Executing the following commands will deploy this example to your CloudFoundry org:

```sh
# Configure the tracing library
$ cf set-env my-app SIGNALFX_SERVICE_NAME <application name>
$ cf set-env my-app SIGNALFX_ENDPOINT_URL <Smart agent or OTel collector address>

# Deploy the app using manifest-linux.yml
$ cf push -f manifest-linux.yml
```

The SignalFx Tracing Library for .NET will be automatically applied to your app by the buildpack.

### Deploying to a Windows stack

You need to [build the application executable](#targetting-windows-stack) before deployment.

After you have built the Windows executables you can now run the following commands to deploy this example to your CloudFoundry org:

```sh
# Use manifest-windows.yml as the application manifest
$ cp manifest-windows.yml publish/manifest.yml
$ cd publish

# Configure the tracing library
$ cf set-env my-app SIGNALFX_SERVICE_NAME <application name>
$ cf set-env my-app SIGNALFX_ENDPOINT_URL <Smart agent or OTel collector address>

# Deploy the app
$ cf push
```
