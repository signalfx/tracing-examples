>ℹ️&nbsp;&nbsp;SignalFx was acquired by Splunk in October 2019. See [Splunk SignalFx](https://www.splunk.com/en_us/investor-relations/acquisitions/signalfx.html) for more information.

# Tracing examples for Splunk Observability Cloud

This repository contains tracing examples for Splunk Observability Cloud, formerly
known as SignalFx. Most of the examples use Splunk distributions of OpenTelemetry
instrumentations. To learn more about OpenTelemetry and Splunk Observability Cloud,
see [the official documentation](https://docs.splunk.com/Observability).

## Supported formats

Splunk Observability Cloud and the Splunk Distribution of OpenTelemetry Collector
can ingest spans in the following formats:

- OpenTelemetry Protocol or OTLP (Preferred)
- Zipkin v1 and v2 JSON
- Splunk APM Protocol (SAPM)

For more information, see
[Compatible span formats](https://docs.splunk.com/Observability/apm/apm-spans-traces/span-formats.html).

## Instrumenting an application for Splunk

To instrument an application or service to send traces and spans to Splunk
Observability Cloud, see
[Send traces to Splunk APM](https://docs.splunk.com/Observability/apm/set-up-apm/apm-gdi.html).

# License

The example in this repository are licensed under the terms of the Apache Software License version 2.0. For more details, see [the license file](./LICENSE).

