# PHP Symfony auto-instrumentation example

This example demonstrates using OpenTelemetry PHP auto-instrumentation to collect logs and traces from a Symfony demo application. While traces and logs are automatically collected in this example, it includes an example of manually sending metrics.

## Running the example

### Data sent to Splunk APM

To run the example and send its data to Splunk APM, run the following command in `compose-splunk` directory:

```sh
SPLUNK_ACCESS_TOKEN=<access_token> SPLUNK_REALM=<realm> docker-compose up
```

The value for `SPLUNK_ACCESS_TOKEN` can be found
[here](https://app.signalfx.com/o11y/#/organization/current?selectedKeyValue=sf_section:accesstokens).
Reference: [docs](https://docs.splunk.com/Observability/admin/authentication-tokens/api-access-tokens.html#admin-api-access-tokens).

The value for `SPLUNK_REALM` can be found
[here](https://app.signalfx.com/o11y/#/myprofile).
Reference: [docs](https://docs.splunk.com/Observability/admin/allow-services.html).

### Data logged to console

To run the example and see the collected traces, logs and metrics in console as logged by the collector, run the following command in `compose-log` directory:

```sh
docker-compose up
```

### Generating traces and logs

Traces and logs are auto-instrumented, and the Docker Compose configuration exposes the demo application on host port 8080, therefore to generate traces and logs, simply navigate to http://localhost:8080/en/blog/.

### Generating metrics

The [custom controller](./image-php/files/DemoController.php) included in this example adds controller endpoints that create metrics manually by directly using the OpenTelemetry PHP SDK.

Navigate to http://localhost:8080/en/demo/metric-count to increment a sample counter metric, or http://localhost:8080/en/demo/metric-gauge?value=33 to set the value of a gauge metric to a specified value.

## Integration with an existing application

To reproduce the behavior on this demo on an existing Symfony application, the following steps need to be taken:
- `protobuf` and `opentelemetry` PHP extensions need to be installed
- OpenTelemetry SDK, exporter, and Symfony and PSR-3 auto-instrumentation dependencies need to be added to `composer.json`
- OpenTelemetry SDK needs to be configured to be autoloaded, and exporting needs to be configured

### Installing extensions

Installing the required extensions can be done through `pecl` if available:
```sh
pecl config-set preferred_state beta
pecl install protobuf
pecl install opentelemetry
```

And these extensions also need to be enabled via an `.ini` file;
```
extension=protobuf.so
extension=opentelemetry.so
```

### Adding dependencies

Dependencies required for instrumentation (only include PSR-3 if log exporting is needed):

```
"open-telemetry/sdk": "^1.0",
"open-telemetry/opentelemetry-auto-symfony": "1.0.0beta22",
"open-telemetry/opentelemetry-auto-psr3": "^0.0.6",
```

Dependencies required for OTLP export using `http/protobuf` protocol:

```
"open-telemetry/exporter-otlp": "^1.0",
"php-http/guzzle7-adapter": "^1.0",
```

### Configuration

Configuration is done via environment variables. The best method to set these depends on how the PHP application is launched.

Environment variables to enable SDK autoloading:
```
OTEL_PHP_AUTOLOAD_ENABLED=true
```

Environment variables to export all signals via OTLP:
```
OTEL_EXPORTER_OTLP_ENDPOINT=http://<collector-host>:4318
OTEL_TRACES_EXPORTER=otlp
OTEL_METRICS_EXPORTER=otlp
OTEL_LOGS_EXPORTER=otlp
```

Environment variables to enable collecting PSR-3 logs:
```
OTEL_PHP_PSR3_MODE=export
```
