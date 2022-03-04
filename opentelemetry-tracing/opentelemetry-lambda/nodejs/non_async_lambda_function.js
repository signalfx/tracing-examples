'use strict';

exports.lambda_handler = function(event, ctx, callback) {

  const api = require('@opentelemetry/api');
  let span = api.trace.getSpan(api.context.active());
  let context = span.spanContext()

  console.log('Received event: '+JSON.stringify(event))
  console.log(`OpenTelemetry tracing enabled. Active spanId=${context.spanId}, traceId=${context.traceId}`)

  const response = {
    statusCode: 200,
    body: 'Hello from lambda'
  };
  callback(undefined, response);
};