require 'opentelemetry/sdk'

def lambda_handler(event:, context:)

  span = OpenTelemetry::Trace.current_span
  span_context = span.context

  puts "OpenTelemetry tracing enabled. Active spanId=#{span_context.hex_span_id}, traceId=#{span_context.hex_trace_id}"

  { statusCode: 200, body: "traceId=#{span_context.hex_trace_id} "  }
end
