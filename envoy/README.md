# Envoy Tracing

Envoy comes out of the box with a Zipkin tracer that will send spans for
all HTTP traffic that goes through the proxy.

The [example envoy config](./envoy.yaml) extends the standard google.com proxy
that is used in the Envoy documentation to send spans for the requests to
SignalFx.  All you have to do to get it working is to replace `MY_ACCESS_TOKEN`
with your organization's SignalFx access token.  Then you can run it in Envoy
using Docker:

```
docker run --rm -it -v $(pwd)/envoy.yaml:/etc/envoy/envoy.yaml -p 10000:10000 envoyproxy/envoy:latest
```

If you hit `localhost:10000` it should show you the Google homepage and then
you should see traces in SignalFx that show the various requests made to
Google.

## Watch out for the `http_connection_manager`'s configuration

The only catch is that the config for your `http_connection_manager`s
**must** have a `tracing` config with the `operation_name` set in it
(see the links below for more details). `operation_name` can either be
`ingress` or `egress` depending on whether the connection is for inbound
traffic or outbound traffic. This will determine the kind of span that
gets sent so it is important to set it correctly. If they do not have
that field, spans will not be emitted for those connections.

## Resources

- [Zipkin tracing config](https://www.envoyproxy.io/docs/envoy/latest/api-v2/config/trace/v2/trace.proto#config-trace-v2-zipkinconfig)
- [http_connection_manager tracing config](https://www.envoyproxy.io/docs/envoy/latest/api-v2/config/filter/network/http_connection_manager/v2/http_connection_manager.proto.html?highlight=operation_name#envoy-api-msg-config-filter-network-http-connection-manager-v2-httpconnectionmanager-tracing)
