# SignalFx Distributed Tracing Instrumentation

## Instrumentation Agnostic

SignalFx Distributed Tracing intends to be instrumentation agnostic; supporting
& ingesting variety of popular open instrumentation libraries including
[OpenTracing](http://opentracing.io/), [Zipkin](https://zipkin.io/) &
[OpenCensus](https://opencensus.io/). So long as the tracer configured to send
spans to SignalFx ingest endpoint uses the Zipkin v1/2 JSON wire format or
Jaeger Thrift format, we will accept spans irrespective of how they were
instrumented - via one of the above mentioned open libraries or a homegrown one.
Our goal is to build on the shoulders of giant communities rather than re-invent
the wheel with proprietary libraries/agents and give customers choice to decide
what works for them without worrying about vendor lock-in.

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

RPC layer(s) and service/web framework(s) are the best places to start when
thinking about how to go about instrumentating your application - both of these
will likely have a large coverage area and touch a significant number of
transaction paths to give you baseline tracing coverage and visualize an
end-to-end trace with a service-map.

Next you should identify services critical to your business and look for areas
not covered by rpc or service/web frameworks. Within these services, identify
high value transactions and critical paths - instrument enough of these.

- [Guide to help strategize instrumentation for your distributed application](http://opentracing.io/documentation/pages/instrumentation/instrumenting-large-systems.html)
- [RPC and Web Frameworks pre-instrumented with OpenTracing](http://github.com/opentracing-contrib)
- [Service-Mesh is another increasingly popular way to quickly get rpc/inter-service visibility](http://istio.io/docs/tasks/telemetry/distributed-tracing/)


## Examples

This repository contains a set of simple example applications that demonstrate
using OpenTelemetry or SignalFx with various open source tracers.
They are broken down by language/platform.

### .NET

 - [SignalFx.Tracing](./dotnet-manual-instrumentation/README.md): An example using the
[SignalFx.Tracing.OpenTracing NuGet Package](https://www.nuget.org/packages/SignalFx.Tracing.OpenTracing/) to
instrument .NET libraries and applications.

### Java

- [OpenTelemetry Java](./opentelemetry-tracing/opentelemetry-java-tracing): our recommended tracer for Java.

### Python

- [OpenTelemetry Python](./opentelemetry-tracing/opentelemetry-python-tracing): our recommended tracer for Python.

### Go

- [SignalFx Go](./signalfx-tracing/signalfx-go-tracing): SignalFx tracer for Golang.

### Service Meshes

- Istio
  - [Istio Mixer Adapter](./istio): We have an out of process adapter available for Istio.
    This is an example configuration for that adapter.
  - [Istio E-Commerce Application](./service-mesh/istio): An example application
    with Istio tracing using the SignalFx adapter and Envoy tracing.
- [Envoy](./envoy): the Envoy proxy can be configured to report trace spans to
  SignalFx.
- [AWS App Mesh](./service-mesh/appmesh): An example E-Commerce application with
  deployment and configuration files for App Mesh on ECS.

## Auto-Instrumentation

For customers who have not instrumented their applications, or have done so in
an OpenTracing-compatible fashion, we offer several SignalFx Tracing libraries.
Their detailed documentation is available in their respective source locations:

- [.NET](https://github.com/signalfx/signalfx-dotnet-tracing)
- [Go](https://github.com/signalfx/signalfx-go-tracing)
- [PHP](https://github.com/signalfx/signalfx-php-tracing)

Examples of those auto-instrumentation techniques are available in this
repository:

- [SignalFx Tracing Examples](./signalfx-tracing)
