# This simple Lambda example using a Ruby handler takes a request body with
# {
#   body: "Some message",
#   queryStringParameters: {
#     limit: 100
#   }
# }
# and returns the sum of all prime numbers below 'limit'.

require 'opentracing'
require 'jaeger/client'

def handle_request(event:, context:)
  # keep the tracer reporter so we can flush out all spans before finishing
  reporter = create_tracer

  # check for a propagated span context if headers are included in this event
  # at the moment, the API gateway proxy does not use a case-insensitive container for headers
  headers = event['headers'] ? to_lowercase(event['headers']) : {}
  span_context = OpenTracing.global_tracer.extract(OpenTracing::FORMAT_TEXT_MAP, headers)

  sum = 0

  # tracing scope can be created and managed automatically
  OpenTracing.global_tracer.start_active_span("handle_request", child_of: span_context) do |scope|

    # read the body field from the event and tag the span with it
    body = event['body']
    scope.span.set_tag("body", body)

    # perform the main work of this function
    limit = event['queryStringParameters']['limit']
    sum = prime_sum(limit)
  end

  # flush out the spans to avoid losing any after the function exits
  reporter.flush

  # when using the default API Gateway proxy, the function must return a JSON document with statusCode and body.
  # The requirements may differ if the gateway is configured differently
  { statusCode: 200, body: JSON.generate(sum) }
end

# sum up the list of primes and tag the span with the total
def prime_sum(n)
  sum = 0
  OpenTracing.global_tracer.start_active_span("calculate_sum") do |scope|
    primes = sieve(n)
    sum = primes.reduce( :+ )
    scope.span.set_tag("sum", sum)
  end
  sum
end

# take a range of numbers and filter through them until only primes are left
def sieve(n)
  primes = nil

  OpenTracing.global_tracer.start_active_span("sieve") do |scope|
    # don't really want the first two, but matching index to value makes it easier
    primes = [*0..n]
    primes[0] = nil
    primes[1] = nil

    for x in primes do
      next if x.nil? # already marked as composite, skip to the next one

      # find all the composites from this number and make them nil
      (2*x..n).step(x) do |c|
        primes[c] = nil
      end
    end

    # remove the nils from the array
    primes = primes.compact

    # tag the number of primes found
    scope.span.set_tag("primes.count", primes.count)
  end

  primes
end

# this function reads environment variables for configuration and returns a trace reporter
def create_tracer
  # these three environment variables should be set in the console before
  # invoking the function
  access_token = ENV['SIGNALFX_ACCESS_TOKEN']
  ingest_url = ENV['SIGNALFX_INGEST_URL']
  service_name = ENV['SIGNALFX_SERVICE_NAME']

  # configure the http sender to set the access token header and send spans to
  # SignalFx ingest.
  headers = { }
  headers['X-SF-Token'] = access_token if !access_token.empty?
  encoder = Jaeger::Client::Encoders::ThriftEncoder.new(service_name: service_name)
  httpsender = Jaeger::Client::HttpSender.new(url: ingest_url, headers: headers, encoder: encoder, logger: Logger.new(STDOUT))
  reporter = Jaeger::Client::Reporters::RemoteReporter.new(sender: httpsender, flush_interval: 1)

  # Use Zipkin B3 format for span propagation to other services 
  injectors = {
    OpenTracing::FORMAT_TEXT_MAP => [Jaeger::Client::Injectors::B3RackCodec]
  }
  extractors = {
    OpenTracing::FORMAT_TEXT_MAP => [Jaeger::Client::Extractors::B3RackCodec]
  }

  OpenTracing.global_tracer = Jaeger::Client.build(
    service_name: service_name,
    reporter: reporter,
    injectors: injectors,
    extractors: extractors)

  # we need to keep a handle to the reporter so spans can be flushed before exiting
  return reporter
end

# this is just a helper method to quickly convert all hash keys to lowercase
def lowercase_hash(hash)
  hash.inject({}) { |memo, (key, value)| memo[key.downcase] = value; memo }
end
