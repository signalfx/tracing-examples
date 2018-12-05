# Jaeger Go Tracer Example

This is a simple example of using the Jaeger Go tracer with SignalFx.
See [./main.go](./main.go) for the example code.

## Building

To build this example locally, from this directory do the following:

```
$ ln -s $(dirname $(pwd)) $GOPATH/src/github.com/signalfx/tracing-examples
$ cd $GOPATH/src/github.com/signalfx/tracing-examples/jaeger-go
$ go build .
```

Now, to run it you need to configure the Jaeger tracer.  The simplest way to do
this is via environment variables:

```sh
$ # Change this to whatever your app is called
$ export JAEGER_SERVICE_NAME=my-app
$ # This will be different if using the Smart Agent/Gateway deployment model
$ export JAEGER_ENDPOINT=https://ingest.signalfx.com/v1/trace
$ export JAEGER_PASSWORD=<MY_ORG_TOKEN>

$ ./jaeger-go
```

## Resources

- [Jaeger Go Tracer](https://github.com/jaegertracing/jaeger-client-go)
- [OpenTracing Go](https://github.com/opentracing/opentracing-go)
