# SignalFx Distributed Tracing Instrumentation

## Instrumentation Agnostic
SignalFx Distributed Tracing intends to be instrumentation agnostic; supporting &
ingesting variety of popular open instrumentation libraries including Zipkin, 
OpenTracing & OpenCensus. So long as the tracer configured to send spans to SignalFx
ingest endpoint uses the Zipkin v1/2 JSON wire format or Jaeger Thrift format, we will
accept spans irrespective of how they were instrumented - via one of the above mentioned 
open libraries or a homegrown one. Our goal is to build on the shoulders of giant 
communities rather than re-invent the wheel with proprietary libraries/agents and give 
customers choice to decide what works for them without worrying about vendor lock-in.

For customers who have not yet instrumented their code, our default recommendation is
to use OpenTracing to instrument since it has a growing ecosystem of library owners & 
frameworks instrumenting their code with it & use Jaeger tracer libraries to export 
spans to us  - both of these are CNCF projects with a rapidly growing community behind them.


## Examples
This repository contains a set of simple example applications that demonstrate
using SignalFx with various open source tracers.  


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
