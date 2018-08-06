# Jaeger Go Tracer Example

This is a simple example of using the Jaeger Go tracer with SignalFx.
See [./main.go](./main.go) for the example code.

## Building

To run this example locally, from this directory do the following:

```
$ ln -s $(dirname $(pwd)) $GOPATH/src/github.com/signalfx/tracing-examples
$ cd $GOPATH/src/github.com/signalfx/tracing-examples/jaeger-go
$ go build .
$ ./jaeger-go
```

## Resources

- [Jaeger Go Tracer](https://github.com/jaegertracing/jaeger-client-go)
- [OpenTracing Go](https://github.com/opentracing/opentracing-go)
