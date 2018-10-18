require 'sinatra'

require 'opentracing'
require 'jaeger/client'
require 'jaeger/client/http_sender'

# access token extracted from an environment variable
access_token = ENV['SIGNALFX_ACCESS_TOKEN']

# the service name to export all spans under
service_name = "signalfx-jaeger-ruby-example"

# the SignalFx ingest endpoint
ingest_url = "https://ingest.signalfx.com/v1/trace"

# create the header table and encoder needed for the Http Sender
headers = { "X-SF-Token" => access_token }
encoder = Jaeger::Client::Encoders::ThriftEncoder.new(service_name: service_name)
http_sender = Jaeger::Client::HttpSender.new(url: ingest_url, headers: headers, encoder: encoder)

# use the Http Sender when registering the client as the global tracer
OpenTracing.global_tracer = Jaeger::Client.build(service_name: service_name, sender: http_sender)

# handle requests to /
get '/' do
    output = ""
    # start a new span and pass the span's scope to the block
    OpenTracing.start_active_span("/") do |scope|
        # set the method and status code tags
        scope.span.set_tag("http.method", "GET")
        scope.span.set_tag("http.status_code", 200)
        # set the span kind to "server" since this handles an incoming request
        scope.span.set_tag("span.kind", "server")

        # generate the random number for output
        output = "#{random}\n"
    end
    output
end

def random
    num = 0
    OpenTracing.start_active_span("random_number") do |scope|
        # generate a random number less than 1000
        num = rand(1000)
        scope.span.set_tag("number", num)

        # start the sequence
        fibonacci(0, 1, num)
    end
    num
end

def fibonacci(first, second, num)
    OpenTracing.start_active_span("fibonacci") do |scope|
        scope.span.set_tag("sequence_number", first)

        # check whether we have reached a number greater than the generated one
        # and if not, continue
        if second < num
            fibonacci(second, first + second, num)
        end
    end
end

