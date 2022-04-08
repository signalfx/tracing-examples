module server

go 1.16

require (
	github.com/gorilla/mux v1.8.0
	github.com/signalfx/splunk-otel-go/distro v0.8.0
	github.com/signalfx/splunk-otel-go/instrumentation/net/http/splunkhttp v0.8.0
	go.opentelemetry.io/contrib/instrumentation/github.com/gorilla/mux/otelmux v0.31.0
	go.opentelemetry.io/contrib/instrumentation/net/http/otelhttp v0.31.0
)
