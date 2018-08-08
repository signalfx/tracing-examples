# SignalFx Distributed Tracing Instrumentation

## Instrumentation Agnostic

SignalFx Distributed Tracing intends to be instrumentation agnostic; supporting
& ingesting variety of popular open instrumentation libraries including 
[OpenTracing](http://opentracing.io/), [Zipkin](https://zipkin.io/) &
[OpenCensus](https://opencensus.io/). So long as the tracer configured to send spans to
SignalFx ingest endpoint uses the Zipkin v1/2 JSON wire format or Jaeger Thrift
format, we will accept spans irrespective of how they were instrumented - via
one of the above mentioned open libraries or a homegrown one. Our goal is to
build on the shoulders of giant communities rather than re-invent the wheel with
proprietary libraries/agents and give customers choice to decide what works for
them without worrying about vendor lock-in.

<p align="center">
  <a href="https://opentracing.io">
  <img src="https://avatars2.githubusercontent.com/u/15482765?s=100&v=4"
       alt="OpenTracing" /></a>
  &nbsp;&nbsp;
  <a href="https://jaegertracing.io">
  <img src="https://avatars3.githubusercontent.com/u/28545596?s=100&v=4"
       alt="Jaeger Tracing" /></a>
  &nbsp;&nbsp;
  <a href="https://zipkin.io">
  <img src="https://avatars3.githubusercontent.com/u/11860887?s=100&v=4"
       alt="ZipKin" /></a>
    &nbsp;&nbsp;
  <a href="https://opencensus.io">
  <img src="https://avatars3.githubusercontent.com/u/26944525?s=100&v=4"
       alt="OpenCensus" /></a>
</p>

For customers who have not yet instrumented their code, our default
recommendation is to use OpenTracing to instrument since it has a growing
ecosystem of library owners & frameworks instrumenting their code with it & use
Jaeger tracer libraries to export spans to us - both of these are CNCF projects
with a rapidly growing community behind them.

## How should I go about instrumenting my application for distributed tracing?
RPC layer(s) and service/web framework(s) are the best places to start when thinking about
how to go about instrumentating your application - both of these will likely have 
a large coverage area and touch a significant number of transaction paths to give
you baseline tracing coverage and visualize an end-to-end trace with a service-map.

Next you should identify services critical to your business and look for areas 
not covered by rpc or service/web frameworks. Within these services, identify high value
transactions and critical paths - instrument enough of these.


- [Guide to help strategize instrumentation for your distributed application](http://opentracing.io/documentation/pages/instrumentation/instrumenting-large-systems.html)

- [RPC and Web Frameworks pre-instrumented with OpenTracing](http://github.com/opentracing-contrib)

- [Service-Mesh is another increasingly popular way to quickly get rpc/inter-service visibility](http://istio.io/docs/tasks/telemetry/distributed-tracing/)



## Examples

This repository contains a set of simple example applications that demonstrate
using SignalFx with various open source tracers.  They are broken down by
language/platform.

### Java

- [Jaeger Java](./jaeger-java): our recommended tracer for Java.
- [Zipkin Brave](./zipkin-brave-java): Zipkin's instrumentation library for
  Java.
- [Java Kafka tracing](./opentracing-kafka-java): an example of tracing Kafka
  using Zipkin Brave with OpenTracing bindings.

### Python

- [Jaeger Python](./jaeger-python): our recommended tracer for Python.

### Go

- [Jaeger Go](./jaeger-go): our recommended tracer for Golang.

### NodeJS

- [Jaeger Node](./jaeger-nodejs)
- [Zipkin JS Tracer](./zipkin-js): Zipkin's instrumentation library for
  Javascript (currently supports a broader range of instrumentations than the
  Jaeger Node tracer and supports running in a browser).

### Service Meshes

- [Istio](./istio): use the [SignalFx mixer
  adapter](https://istio.io/docs/reference/config/policy-and-telemetry/adapters/signalfx/)
  to automatically send metrics and trace spans for inter-container traffic.
- [Envoy](./envoy): the Envoy proxy can be configured to report trace spans to
  SignalFx.
