# Jaeger Ruby Tracing Example

This simple Sinatra app shows how to use the Jaeger Ruby client. It generates a
random number and creates spans showing the Fibonacci sequence that gets the
closest to that number.


# Setup and configuration

## Install the gem

Clone the [jaeger-client-ruby](https://github.com/signalfx/jaeger-client-ruby) repo and `cd` into it.

```bash
git clone https://github.com/signalfx/jaeger-client-ruby

cd jaeger-client-ruby
```

Set up dependencies and run the install command.

```bash
bin/setup

bundle exec rake install
```

## Configure project

In the application's Gemfile, add this line.

```ruby
gem 'jaeger-client'
```

Add these imports:

```ruby
require 'opentracing'
require 'jaeger/client'
require 'jaeger/client/http_sender'
```

By default, the tracer uses the UDP sender with the Jaeger client. To set up
the HTTP sender to the SignalFx endpoint, it must first be configured and then
passed to the tracer.

```ruby
headers = { "X-SF-Token" => accessToken }

encoder = Jaeger::Client::Encoders::ThriftEncoder.new(service_name: "service_name")
http_sender = Jaeger::Client::HttpSender.new(
    url: "http://ingest.signalfx.com/v1/traces",
    headers: headers
    encoder: encoder)

OpenTracing.global_tracer = Jaeger::Client.build(service_name: "service_name", sender: http_sender)
```


# Creating spans

When creating spans in block form, the scope is passed as an optional argument.

```ruby
OpenTracing.start_active_span("span_name") do |scope|
    ...
end
```

The current span can be accessed from elsewhere with

```ruby
span = OpenTracing.active_span
```

A child span can be started by getting the current active scope and then
starting a new span.

```ruby
scope = OpenTracing.scope_manager.active
span = OpenTracing.start_active_span("child_span", child_of: scope.span)
```

A parent span and child span can be manually managed.

```ruby
parent = OpenTracing.start_active_span("parent_span")
child = OpenTracing.start_active_span("child_span", child_of: parent)
```

For more examples on usage, visit [opentracing-ruby](https://github.com/opentracing/opentracing-ruby)


# Running the example.

Install the gem as shown above in the Setup and Configuration section.

Set the `SIGNALFX_ACCESS_TOKEN` environment variable to a valid token.

```bash
export SIGNALFX_ACCESS_TOKEN=<token>
```

Run the example with

```bash
ruby example.rb
```

To trigger a trace, nagivate to http://localhost:4567/ in a browser, or do

```bash
curl localhost:4567
```

This will return a random number and spans showing the Fibonacci sequence up to the
generated number will be exported.

