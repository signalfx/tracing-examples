# Splunk Distribution of OpenTelemetry .NET log trace correlation examples

This folder contains sample applications instrumented using
[Splunk Distribution of OpenTelemetry .NET](https://github.com/signalfx/splunk-otel-dotnet)
that produce logs using logging API from `Microsoft.Extensions.Logging` enriched with
a trace context.

## Building and running the example setup

This multi-container application assumes you have [Docker Compose](https://docs.docker.com/compose/) installed on your system.
To build and run the example services, clone this repository, move to this folder and from there do the following:

```sh
docker-compose build
SPLUNK_ACCESS_TOKEN=<access_token> SPLUNK_REALM=<realm> SPLUNK_HEC_URL=<splunk_hec_url> SPLUNK_HEC_TOKEN=<splunk_hec_token> docker-compose up
```

The value for `SPLUNK_ACCESS_TOKEN` can be found
[here](https://app.signalfx.com/o11y/#/organization/current?selectedKeyValue=sf_section:accesstokens).
Reference: [docs](https://docs.splunk.com/Observability/admin/authentication-tokens/api-access-tokens.html#admin-api-access-tokens).

The value for `SPLUNK_REALM` can be found
[here](https://app.signalfx.com/o11y/#/myprofile).
Reference: [docs](https://docs.splunk.com/Observability/admin/allow-services.html).

See [Splunk documentation](https://docs.splunk.com/Documentation/Splunk/latest/Data/UsetheHTTPEventCollector) to learn how to obtain
values for `SPLUNK_HEC_URL` and `SPLUNK_HEC_TOKEN`.

## Automatic correlation

Logs are automatically enriched with trace context
and exported to [Splunk OpenTelemetry Collector](https://github.com/signalfx/splunk-otel-collector),
which propagates them to configured Splunk Cloud Platform/Enterprise deployment.

Traces are exported to [Splunk OpenTelemetry Collector](https://github.com/signalfx/splunk-otel-collector),
which propagates them to Splunk Observability Cloud.

When [Log Observer Connect](https://docs.splunk.com/Observability/en/logs/intro-logconnect.html)
is setup between Splunk Cloud/Enterprise and Splunk Observability, logs can be queried using
capabilities of Splunk Log Observer in Splunk Observability Cloud.

### Requirements

- `.NET` application (`.NET Framework` applications are currently not supported)
- `Microsoft.Extensions.Logging` in version `6.0.0` or higher used for logging
- [Splunk OpenTelemetry Collector](https://github.com/signalfx/splunk-otel-collector) deployed with:
  - logs pipeline configured with `otlp` receiver and `splunk_hec` exporter configured to send logs to Splunk Cloud Platform/Enterprise
  - traces pipeline configured with `otlp` receiver and `sapm` exporter configured to send traces to Splunk Observability Cloud
- `OTEL_EXPORTER_OTLP_ENDPOINT` set to address of `otlp` receiver of a deployed Splunk OpenTelemetry Collector instance

See sample Splunk OpenTelemetry Collector [configuration](./otel-config.yaml) for more details.

## Manual correlation

It is possible to inject trace context into current logs destination (e.g file),
but manual modifications of the application code are required.

Specific steps required differ based on logging library used.

### NLog

Configuration using `xml` [`nlog.config`](./Example.LogTraceCorrelation.Web/nlog.config) file is assumed.

Requirements:

- `NLog.DiagnosticSource` package installed
- `NLog.DiagnosticSource` added as an extension in `nlog.config` configuration
- layout of the target used adjusted to include trace context

See `NLog.DiagnosticSource` [documentation](https://github.com/NLog/NLog.DiagnosticSource) for more details.

See [sample application](./Example.LogTraceCorrelation.Web/Program.cs) for an example application
with required setup.

### Serilog

Requirements:

- `Serilog.Enrichers.Span` package installed (or custom enricher created)
- `Serilog` configured to use enricher providing trace context, e.g [`Serilog.Enrichers.Span`](https://www.nuget.org/packages/Serilog.Enrichers.Span)
- output template adjusted to include trace context, if plain text format is used for logging

See `Serilog.Enrichers.Span` [documentation](https://github.com/RehanSaeed/Serilog.Enrichers.Span) for more details.

See [sample application](./Example.LogTraceCorrelation.Console/Program.cs) for an example application
with required setup.
