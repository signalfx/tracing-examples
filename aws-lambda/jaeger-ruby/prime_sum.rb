# This simple Lambda example using a Ruby handler takes a request body with
# {
#   body: "Some message",
#   queryStringParameters: {
#     limit: 100
#   }
# }
# and returns the sum of all prime numbers below 'limit'.

require 'opentracing'
require 'signalfx/lambda'

# this is the original lambda handler
def handle_request(event:, context:)
  sum = 0

  # when using the wrapper, the tracer is available through OpenTracing.global_tracer
  # to do more granular instrumentation
  OpenTracing.global_tracer.start_active_span("handle_request") do |scope|

    # read the body field from the event and tag the span with it
    body = event['body']
    scope.span.set_tag("body", body)

    # perform the main work of this function
    limit = event['queryStringParameters']['limit']
    sum = prime_sum(limit)
  end

  # when using the default API Gateway proxy, the function must return a JSON document with statusCode and body.
  # The requirements may differ if the gateway is configured differently
  { statusCode: 200, body: JSON.generate(sum) }
end

# register the handler with the wrapper
SignalFx::Lambda::Tracing.register_handler(&method(:handle_request))


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

