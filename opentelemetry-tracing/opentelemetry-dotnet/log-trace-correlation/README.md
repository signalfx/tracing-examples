# Splunk Distribution of OpenTelemetry .NET log trace correlation examples

This folder contains sample applications instrumented using
[Splunk Distribution of OpenTelemetry .NET](https://github.com/signalfx/splunk-otel-dotnet)
that produce logs using logging API from `Microsoft.Extensions.Logging` enriched with
a trace context.

## Building and running the example setup

This multi-container application assumes you have [Docker Compose](https://docs.docker.com/compose/) installed on your system.
To build and run the example services, clone this repository, move to this folder and from there do the following:

[`Example.LogTraceCorrelation.Console`](./Example.LogTraceCorrelation.Console/Program.cs) is an example console application
 using `ILogger` with additional manual correlation for `Serilog`.

 [`Example.LogTraceCorrelation.Web`](./Example.LogTraceCorrelation.Web/Program.cs) is an example ASP.NET Core application
  using `ILogger` with additional manual correlation for `NLog`.

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

Sample output logged by the `logging` [exporter](https://github.com/open-telemetry/opentelemetry-collector/tree/68dd7d763b599146ce96d3b936f74666af9db757/exporter/loggingexporter)
of a Splunk OpenTelemetry Collector:

```text
2023-09-21 10:18:58 2023-09-21T08:18:58.025Z    info    ResourceLog #0
2023-09-21 10:18:58 Resource SchemaURL: 
2023-09-21 10:18:58 Resource attributes:
2023-09-21 10:18:58      -> splunk.distro.version: Str(1.0.0.0)
2023-09-21 10:18:58      -> container.id: Str(2fbe563f4e8370c0a9812c886f708650c02be4cda66fbbf0de286c3b90a51525)
2023-09-21 10:18:58      -> telemetry.auto.version: Str(1.0.0)
2023-09-21 10:18:58      -> telemetry.sdk.name: Str(opentelemetry)
2023-09-21 10:18:58      -> telemetry.sdk.language: Str(dotnet)
2023-09-21 10:18:58      -> telemetry.sdk.version: Str(1.6.0)
2023-09-21 10:18:58      -> service.name: Str(Example.LogTraceCorrelation.Web)
2023-09-21 10:18:58      -> deployment.environment: Str(splunk-otel-dotnet-tracing-examples)
2023-09-21 10:18:58      -> service.version: Str(1.0.0)
2023-09-21 10:18:58 ScopeLogs #0
2023-09-21 10:18:58 ScopeLogs SchemaURL: 
2023-09-21 10:18:58 InstrumentationScope  
2023-09-21 10:18:58 LogRecord #0
2023-09-21 10:18:58 ObservedTimestamp: 2023-09-21 08:18:53.5620384 +0000 UTC
2023-09-21 10:18:58 Timestamp: 2023-09-21 08:18:53.5620384 +0000 UTC
2023-09-21 10:18:58 SeverityText: Information
2023-09-21 10:18:58 SeverityNumber: Info(9)
2023-09-21 10:18:58 Body: Str(Request received.)
2023-09-21 10:18:58 Trace ID: 85b3787ac1c9b7142bcfc1bd6b5e82aa
2023-09-21 10:18:58 Span ID: 2844a3cc560bc7eb
2023-09-21 10:18:58 Flags: 1
```

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

Sample output:

```text
2023-09-21 06:44:09.8004|Info|Request received.|TraceId=3529d39089617418f137ae30a6aa1b33|SpanId=4fa9a57a746e9a84|TraceFlags=Recorded|
```

See `NLog.DiagnosticSource` [documentation](https://github.com/NLog/NLog.DiagnosticSource) for more details.

See [sample application](./Example.LogTraceCorrelation.Web/Program.cs) for an example application
with required setup.

### Serilog

Requirements:

- `Serilog.Enrichers.Span` package installed (or custom enricher created)
- `Serilog` configured to use enricher providing trace context, e.g [`Serilog.Enrichers.Span`](https://www.nuget.org/packages/Serilog.Enrichers.Span)
- output template adjusted to include trace context, if plain text format is used for logging

Sample output:

```text
[06:44:09 INF] Request finished.|TraceId=3529d39089617418f137ae30a6aa1b33|SpanId=d1768f86e17d3bf5|TraceFlags=Recorded
```

See `Serilog.Enrichers.Span` [documentation](https://github.com/RehanSaeed/Serilog.Enrichers.Span) for more details.

See [sample application](./Example.LogTraceCorrelation.Console/Program.cs) for an example application
with required setup.
