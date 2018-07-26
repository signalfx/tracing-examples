# SignalFx Distributed Tracing Examples

This repository contains a set of simple example applications that demonstrate
using SignalFx with various open source tracers.  We do not yet have our own
tracer, so you must use a third-party tracer configured to send to the SignalFx
ingest server using the Zipkin v1/2 JSON or Jaeger Thrift format.

## Examples

### Jaeger client libraries

 - [Java](./jaeger-java)
 - [Go](./jaeger-go)

### Zipkin tracer libraries

 - [Brave Java Tracer](./zipkin-brave-java)
 - [Zipkin JS Tracer](./zipkin-js)

### Service Meshes

 - [Istio](./istio) - We have a mixer adapter that will automatically sent
     trace spans for inter-container traffic.
 - [Envoy](./envoy) - You can configure Envoy to send us trace spans.

### OpenTracing ecosystem

 - [Java Kafka tracing](./opentracing-kafka-java)
