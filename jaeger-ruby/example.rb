require 'sinatra'

require 'opentracing'
require 'jaeger/client'
require 'jaeger/client/http_sender'

access_token = ENV['SIGNALFX_ACCESS_TOKEN']
service_name = "signalfx-jaeger-ruby-example"
ingest_url = "http://ingest.signalfx.com/v1/trace"
headers = { "X-SF-Token" => access_token }

encoder = Jaeger::Client::Encoders::ThriftEncoder.new(service_name: service_name)
http_sender = Jaeger::Client::HttpSender.new(url: ingest_url, headers: headers, encoder: encoder)
OpenTracing.global_tracer = Jaeger::Client.build(service_name: service_name, sender: http_sender)

get '/' do
    "#{random}\n"
end

def random
    OpenTracing.start_active_span("random_number") do |scope|
        num = rand(1000)
        scope.span.set_tag("number", num)
        fibonacci(0, 1, num)
    end
    num
end

def fibonacci(first, second, num)
    OpenTracing.start_active_span("fibonacci") do |scope|
        scope.span.set_tag("sequence_number", first)

        if second < num
            fibonacci(second, first + second, num)
        end
    end
end

