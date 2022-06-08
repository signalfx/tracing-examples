# OpenTelemetry .NET SDK Example

This example is a simple inventory system that is instrumented via
the [OpenTelemetry .NET](https://opentelemetry.io/docs/instrumentation/net/).

## Building and running the example app and client

This multi-container application assumes you have [Docker Compose](https://docs.docker.com/compose/) installed and on your system. To build and run the example services, please clone this repository and from this directory do the following:

```sh
docker-compose build
SPLUNK_ACCESS_TOKEN=<access_token> SPLUNK_REALM=<realm> docker-compose up
```

The value for `SPLUNK_ACCESS_TOKEN` can be found
[here](https://app.signalfx.com/o11y/#/organization/current?selectedKeyValue=sf_section:accesstokens).
Reference: [docs](https://docs.splunk.com/Observability/admin/authentication-tokens/api-access-tokens.html#admin-api-access-tokens).

The value for `SPLUNK_REALM` can be found
[here](https://app.signalfx.com/o11y/#/myprofile).
Reference: [docs](https://docs.splunk.com/Observability/admin/allow-services.html).

These commands will build the .NET projects and provide and coordinate their environments to create instrumented
request activity to be reported to Splunk Observability. The traced activity is initiated by the ExampleClient running through a
perpetual cycle of CRUD operations, which take place via the AspNetCoreExample ASP.NET Core application and its
associated MongoDB driver.

You can find the collected traces in Splunk Observability Cloud: <https://app.signalfx.com/#/apm?environments=YOURUSERNAME>

> Note: Processing might take some time.
