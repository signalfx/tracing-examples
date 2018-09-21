# Opencensus Jaeger Exporter for Go

This is a simple example using the Jaeger exporter from Opencensus to send spans
to SignalFx. The code is in [main.go](main.go)


# Setup and Usage

## Dependencies

Import the following packages:
```go
import (
    "go.opencensus.io/exporter/jaeger"
    "go.opencensus.io/trace"
)
```

## Creating an exporter

```go
exporter, err := jaeger.NewExporter(jaeger.Options{
    CollectorEndpoint: ingestUrl,
    Process: jaeger.Process{
        ServiceName: serviceName,
    },
    Username: "auth",
    Password: accessToken,
})
defer exporter.Flush()
```

Afterwards, register and configure the exporter
```go
trace.RegisterExporter(exporter)
trace.ApplyConfig(trace.Config{DefaultSampler: trace.AlwaysSample()})
```

## Creating spans

First, we need a context. If this is a parent span, get a empty context.
```go
ctx := context.Background()
```

If this is a child span being created, we can take a context returned from the
parent span and use that.

Using the context, start the span.
```go
ctx, span := trace.StartSpan(ctx, "span-name")
defer span.End()
```
This creates a span as well as the context for the new span. This context can be
used to associate child spans with this as its parent.

`Span.End()` is used to close a span. Using `defer` simplifies handling it,
unless the span crosses multiple methods.


## Tags and annotations

Spans can be tagged with useful and interesting values before being exported.
The three currently supported types are `bool`, `int64`, and `string`.
```go
span.AddAttributes(
    trace.BoolAttribute("has_error", error),
    trace.Int64Attribute("error_count", count),
    trace.StringAttribute("error_message", message),
)
```

Another useful way to attach information with a span is an annotation.
Annotations are a list of attributes that have a timestamp relative to the
start of the span.
```go
attributes := []trace.Attribute{
	trace.Int64Attribute("count", int64(count)),
	trace.StringAttribute("full_timestamp", time.Now().String()),
}

span.Annotate(attributes, "annotation")
```


# Running the example

```go
export GOPATH=`pwd`
go get ./...
go run main.go
```

This will start a server listening to port 8080. Navigate to
<http://localhost:8080/test>.

Each request will have a span, during which the app will generate a random
number. It will then increment by 1 up to that number, generating a child span
for each increment.


# Resources

- https://godoc.org/go.opencensus.io/exporter/jaeger
