# SignalFx Distributed Tracing & APM Early Access Program

This document is an introduction and a guide to SignalFx's upcoming
Distributed Tracing & APM features, now available to a few select users
through as part of an Early Access Program (EAP).

As you've been made aware, SignalFx is working on a new facet of its
monitoring solution for modern applications. Distributed systems tracing
offers unique insights into application performance and troubleshooting
that complements a metrics-based monitoring approach and that can
deliver additional value when supported by a class-leading real-time
metrics analytics platform.

Akin to our approach for metrics instrumentation, SignalFx intends to
remain as instrumentation-agnostic as possible, leveraging open-source
and open standards for client-side tracing instrumentation and wire
formats. SignalFx will provide powerful real-time data exploration
capabilities and easy-to-use trace visualization and analysis features.

<p align="center">
  <a href="https://signalfx.com">
  <img src="https://avatars2.githubusercontent.com/u/8184587?s=100&v=4"
       alt="SignalFx" title="SignalFx" /></a>
  &nbsp;&nbsp;
  <a href="https://opentracing.io">
  <img src="https://avatars2.githubusercontent.com/u/15482765?s=100&v=4"
       alt="OpenTracing" /></a>
  &nbsp;&nbsp;
  <a href="https://zipkin.io">
  <img src="https://avatars3.githubusercontent.com/u/11860887?s=100&v=4"
       alt="ZipKin" /></a>
</p>

This EAP is intended to give you access to those new features as we're
building them. As such, they should be considered "alpha quality": you
may experience bugs or user experience nits along the way. We'll be
looking forward to your feedback as we continue to build this new and
exciting facet of SignalFx's offering.

## Feedback and support requests

For the duration of this Early Access Program for our Distributed
Tracing & APM features, please refrain from using our normal support
channels and instead directly email all feedback and questions to
`tracing-feedback@signalfx.com`.

## Sending trace data to SignalFx

SignalFx's ingest API now exposes a new endpoint for ingesting trace
data, available at `https://ingest.signalfx.com/v1/trace`. As of June
2018, this endpoint only accepts lists of spans encoded in [Zipkin's v2
JSON format](https://zipkin.io/zipkin-api/).

Like the rest of SignalFx's APIs, you need to provide a valid
`X-SF-Token` header. You may also compress the payload with GZip and set
the `Content-Encoding: gzip` as necessary.

The endpoint will return a JSON document describing how many spans were
valid and accepted, and what spans were rejected (and for what reason),
if any.

```
POST /v1/trace HTTP/1.1
Host: ingest.signalfx.com
X-SF-Token: <your-token>
Content-Type: application/json

[{...},{...},{...}]

HTTP/1.1 200 OK
Content-Length: 24
Content-Type: application/json; charset=utf-8

{"invalid":{},"valid":3}
```

### Span validation

SignalFx checks several elements of the received spans to make sure they
are valid and respect certain limitations. Spans that do not comply to
the following rules are not accepted, and their span IDs are provided in
the trace ingest API's response.

* the span's `id` must be present and must be a valid 16-character
  hexadecimal string
* the `traceId` must be present and must be a valid 16-character or
  32-character hexadecimal string
* the `parentSpanId`, if provided, must be a valid 16-character
  hexadecimal string
* the span's `name` must be present and must be a unicode string no
  longer than 1024 characters, and that does not contain any single or
  double quotes (same rules as for metric names)
* the span may contain no more than 128 key/value pair `tags`
* tag keys must be unicode strings no longer than 128 characters and
  cannot start with `_` or `sf_`
* tag values must be unicode strings no longer than 1024 characters
* the span may contain no more than 128 `annotations`, and annotation
  values must be unicode strings no longer than 1024 characters

### Sending spans from code

Using the `jaeger-client-java` library version 0.28.0 or above, you can
create an OpenTracing-compatible `Tracer` that reports spans to SignalFx
using the following pattern. For a more complete example, refer to the
[Jaeger Java](./jaeger-java/) example.

```java
String ingestUrl = "https://ingest.signalfx.com/v1/trace";
String accessToken = "...";
String serviceName = "...";

OkHttpSender.Builder senderBuilder = OkHttpSender.newBuilder()
        .compressionEnabled(true)
        .endpoint(ingestUrl);

senderBuilder.clientBuilder().addInterceptor(chain -> {
    Request request = chain.request().newBuilder()
            .addHeader("X-SF-Token", accessToken)
            .build();
    return chain.proceed(request);
});

OkHttpSender sender = senderBuilder.build();

Tracer tracer = new Tracer.Builder(serviceName)
        // Configure the sampler as desired.
        .withSampler(new ConstSampler(true))
        .withReporter(new Zipkin2Reporter(AsyncReporter.create(sender)))
        .build();
```

For more examples using Zipkin libraries, or in other languages, see the
[README.md](./README.md).

## Visualizing a trace

Trace search is not yet available in this EAP. To visualize a trace in
SignalFx, you'll need to know its trace ID. Then go to
[`https://app.signalfx.com/#/trace`](https://app.signalfx.com/#/trace)
and put in the trace ID in the input field at the top left of the page.
Alternatively, you can go directly to
`https://app.signalfx.com/#/trace/<trace-id>`.

_Screenshot of trace view page's input field_

### Navigating the trace

_Screenshot of minimap and main viewport_

### Trace and span metadata

_Screenshot of sidebar_
