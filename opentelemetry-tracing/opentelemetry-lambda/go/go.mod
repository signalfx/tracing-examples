module github.com/signalfx/splunk-otel-lambda/go/example

go 1.17

require (
	github.com/aws/aws-lambda-go v1.30.0
	github.com/go-logr/stdr v1.2.2
	github.com/signalfx/splunk-otel-go/distro v0.8.0
	go.opentelemetry.io/contrib/instrumentation/github.com/aws/aws-lambda-go/otellambda v0.31.0
	go.opentelemetry.io/otel v1.6.1
	go.opentelemetry.io/otel/exporters/otlp/otlptrace/otlptracehttp v1.6.1
	go.opentelemetry.io/otel/sdk v1.6.1
)
