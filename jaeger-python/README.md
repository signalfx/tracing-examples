# Jaeger Python Tracer Example

This is an example of building distributed traces using the Jaeger Python tracer
with SignalFx. See the instrumented client and server in
[./pi_trace](./pi_trace) for a basic pattern of tracing across services.

## Building

To run this example locally, from this directory do the following:

```
$ pip install -r requirements.txt
$ # Run the server in the background (or from another shell)
$ python pi_trace/pi_server.py -a <OptionalOrgAccessToken> &
$ # Launch the client
$ python pi_trace/pi_client.py -a <OptionalOrgAccessToken>
```

## Resources

- [Jaeger Bindings for Python OpenTracing
  API](https://github.com/jaegertracing/jaeger-client-python): the primary
  tracer implementation used in this example, a SignalFx fork of which provides
  a required HTTPSender and OpenTracing 2.0 API adoption.
- [OpenTracing
  Python](https://github.com/opentracing/opentracing-python): the Jaeger Python
  tracer conforms to this interface, which is intended to be used by
  instrumented libraries and applications when possible over tracer
  implementation-dependent functionality.  It also provides helpful scope management
  utilities for automatically creating trace span relationships throughout your application.

## Other Resources

Here is a good tutorial on OpenTracing and general Jaeger Python client usage:
https://github.com/yurishkuro/opentracing-tutorial/tree/master/python
