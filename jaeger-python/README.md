# Jaeger Python Tracer Example

This is an example of building distributed traces using the Jaeger Python tracer
with SignalFx.  See the instrumented client and server in [./pi_trace](./pi_trace) for a basic pattern of tracing
across services.

## Building

To run this example locally, from this directory do the following:

```
$ pip install -r requirements.txt
$ # Run the server in the background (or from another shell)
$ python pi_trace/pi_server.py -a MyOrgAccessToken &
$ # Launch the client
$ python pi_trace/pi_client.py -a MyOrgAccessToken
```

## Resources

[Jaeger Bindings for Python OpenTracing API](https://github.com/jaegertracing/jaeger-client-python) - The
primary tracer implementation used in this example, a SignalFx fork of which provides a required HTTPSender.

[OpenTracing Python Instrumentation](https://github.com/uber-common/opentracing-python-instrumentation) - A
helper library for useful span context storage and retrieval, as well as instrumented libraries that are unused by
this example.

[OpenTracing Python](https://github.com/opentracing/opentracing-python) - The Jaeger Python
tracer conforms to this interface, which is intended to be used by instrumented libraries and applications when
possible over tracer implementation-dependent functionality.

## Other Resources

Here is a good tutorial on OpenTracing and general Jaeger Python client usage:
https://github.com/yurishkuro/opentracing-tutorial/tree/master/python
