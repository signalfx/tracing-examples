# ASP.NET Core and MongoDb Auto-Instrumentation Example

This is an example of automatically producing distributed traces using the
[SignalFx Tracing Library for .NET Core on Linux](https://github.com/signalfx/signalfx-dotnet-tracing).
Please examine the instrumented [HttpClient](./src/ExampleClient/Program.cs) and [ASP.NET Core application](./src/AspNetCoreExample/Services/ItemService.cs)
for custom instrumentation patterns using the OpenTracing API. This example is of a simple
inventory system that is auto-instrumented via [configuration of the CLR Profiler and tracing library](./src/AspNetCoreExample/Dockerfile).

## Building and running the example app and client

This multi-container application assumes you have [Docker Compose](https://docs.docker.com/compose/) installed and on your system and you've
[configured the Smart Gateway in a docker image](https://docs.signalfx.com/en/latest/apm/apm-deployment/smart-gateway.html#running-the-smart-gateway-as-a-docker-container)
named `smart-gateway`. To build and run the example services, please clone this repository and from this directory do the following:

```bash
  $ docker-compose build
  $ docker-compose up
```

These commands will build the .NET projects and provide and coordinate their environments to create instrumented
request activity to be reported to SignalFx µAPM.  The traced activity is initiated by the ExampleClient running through a
perpetual cycle of CRUD operations, which take place via the AspNetCoreExample ASP.NET Core application and its
associated MongoDb driver.