# Jaeger Node.js Tracer Example

This is a simple example of using the Jaeger Node.js tracer with SignalFx.  See
[./index.js](./index.js) for the example code.

## Building

To run this example locally, you will need a relatively recent version of
Node.js installed.  Then, from this directory do the following:

```
$ npm install
$ export SIGNALFX_ACCESS_TOKEN=<MY_ACCESS_TOKEN>
$ node index.js
```

## Resources

- [Jaeger Node.js Tracer](https://github.com/jaegertracing/jaeger-client-node) - The
primary tracer implementation used in this example.
- [OpenTracing Javascript](https://github.com/opentracing/opentracing-javascript) -
The Jaeger tracer conforms to this interface and should be used whenever
possible.  This example shows the use of the OpenTracing interface.
- [OpenTracing Javascript
Instrumentations](https://github.com/opentracing-contrib?utf8=%E2%9C%93&q=javascript-&type=&language=) - A list of official OT instrumentation libraries for JS
