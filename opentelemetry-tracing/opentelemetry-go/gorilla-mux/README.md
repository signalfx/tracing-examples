# Example

This example instruments a simple HTTP server and client application
using:

- [Splunk Distribution of OpenTelemetry Go](https://github.com/signalfx/splunk-otel-go)
- [OpenTelemetry Go](https://github.com/open-telemetry/opentelemetry-go)
- [OpenTelemetry Go Contrib](https://github.com/open-telemetry/opentelemetry-go-contrib)

The example instruments following the following libraries:

- [`http`](https://pkg.go.dev/http)
- [`github.com/gorilla/mux`](https://pkg.go.dev/github.com/gorilla/mux)

Both applications are configured to send spans to a local instance
of the [Splunk OpenTelemetry Collector](https://github.com/signalfx/splunk-otel-collector),
which propagates them to Splunk Observability Cloud.

## Prerequisites

- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Usage

```sh
SPLUNK_ACCESS_TOKEN=<access_token> SPLUNK_REALM=<realm> ./run.sh
```

The value for `SPLUNK_ACCESS_TOKEN` can be found
[here](https://app.signalfx.com/o11y/#/organization/current?selectedKeyValue=sf_section:accesstokens).
Reference: [docs](https://docs.splunk.com/Observability/admin/authentication-tokens/api-access-tokens.html#admin-api-access-tokens).

The value for `SPLUNK_REALM` can be found
[here](https://app.signalfx.com/o11y/#/myprofile).
Reference: [docs](https://docs.splunk.com/Observability/admin/allow-services.html).

You can find the collected traces in Splunk Observability Cloud: <https://app.signalfx.com/#/apm?environments=YOURUSERNAME>

> Note: Processing might take some time.
