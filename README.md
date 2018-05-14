# SignalFx Distributed Tracing Examples

This repository contains a set of simple example applications that demonstrate
using SignalFx with various open source tracers.  We do not yet have our own
tracer, so you must use a third-party tracer configured to send to the SignalFx
ingest server using the Zipkin v2 JSON format.

## Examples

### Jaeger client libraries

 - [Java](./jaeger-java)
 - [Go](./jaeger-go)

### Zipkin client libraries

 - [Brave Java Client](./zipkin-brave-java)

### OpenTracing ecosystem

 - [Java Kafka tracing](./opentracing-kafka-java)
