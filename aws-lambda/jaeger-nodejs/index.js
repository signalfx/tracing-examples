// This Lambda function is an example of an OpenTracing-instrumented Roulette
// game using the Jaeger Node.Js tracer where requests containing a chosen number
// made to an API Gateway resource are compared to a random number from the wheel.
//
// GET /my_resource/?choice=36 -> 404 Loss
// GET /my_resource/?choice=00 -> 200 Win
//
// Winning requests will produce a logged exception.
//
const { initTracer } = require('jaeger-client');
const opentracing = require('opentracing');


// Roulette address/position maps, helpful for "00"
const choiceToNum = { '00': 37 };
for (let i = 0; i < 37; i++) {
  choiceToNum[i] = i;
}

const numToChoice = [];
Object.entries(choiceToNum).forEach(([k, v]) => { numToChoice[v] = k; });

exports.requestHandler = function (event, context, callback) {
  const response = handleRequest(event, context);
  callback(response.error, response.response);
};

// handle a RequestResponse client invocation.  For asynchronous
// invocations use your own event attributes as needed. 
function handleRequest(event, context) {
  const tracer = createTracer();
  const rootSpan = tracer.startSpan('handleRequest');

  // Here we get our current traceId to include in response
  // for ease of demo trace retrieval only.
  const traceId = getTraceId(rootSpan);

  // Tag execution context information about environment and
  // RequestResponse invocation for future debugging and
  // analytics: https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-context.html
  rootSpan.setTag('awsRequestId', context.awsRequestId);
  rootSpan.log({ remainingTimeInMillis: context.getRemainingTimeInMillis() });

  const choice = getChoice(event, rootSpan);

  let result, statusCode, error = null;
  try {
    // A span's context is needed for establishing references
    result = playRoulette(choice, rootSpan);
    statusCode = 404;
  } catch (err) {
    result = 'You Won!';
    statusCode = 200;
    error = err;
    rootSpan.setTag(opentracing.Tags.ERROR, true);
    rootSpan.log({
      event: 'error',
      'error.object': err,
      'error.message': result,
      'error.stack': err.stack,
    });
  }
  rootSpan.setTag('result', result);

  const response = {
    statusCode,
    body: JSON.stringify({ traceId, result, choice }),
  };

  rootSpan.finish();
  tracer.close();

  return { error, response };
}

function createTracer() {
  const accessToken = process.env.SIGNALFX_ACCESS_TOKEN;
  if (!accessToken) {
    throw new Error('You must set the SIGNALFX_ACCESS_TOKEN Lambda environment variable to be your token.');
  }

  const config = {
    serviceName: 'signalfx-lambda-node-example',
    // Configures a constant sampler such that all invocations will be reported.
    // If desired, use type: 'probabilistic' with a param specifying the percentage of
    // traces to be reported:
    // sampler: {
    //   type: 'probabilistic',
    //   param: .01  // 1% of traces, with 1.0 being equivalent to a constant sampler
    // },
    // See: https://github.com/jaegertracing/jaeger-client-node/blob/master/src/samplers/probabilistic_sampler.js
    sampler: {
      type: 'const',
      param: 1,
    },
    reporter: {
      collectorEndpoint: 'https://ingest.signalfx.com/v1/trace',
      // SignalFx supports Basic authentication with username "auth" and access token as password
      username: 'auth',
      password: accessToken,
    },
  };

  const options = { logger: console };
  const tracer = initTracer(config, options);
  // Register our tracer instance as the global tracer for easy access
  // throughout Lambda function.
  opentracing.initGlobalTracer(tracer);

  return tracer;
}

// Pass spans as arguments for usage within functions
function getTraceId(span) {
  // Per OT: Span contexts are tracer-specific so this
  // will likely not be functional for other tracers
  // and should not be relied on as a stable api.
  const context = span.context();
  function pad(num) {
    return num.toString(16).padStart(8, '0');
  }
  const high = context.traceId.readUInt32BE();
  const low = context.traceId.readUInt32BE(4);
  return pad(high) + pad(low);
}

function getChoice(event, span) {
  const queryStringParameters = event.queryStringParameters || {};
  let { choice } = queryStringParameters;
  if (!choiceToNum.hasOwnProperty(choice)) {
    choice = getRandomPosition();
    const invalid = `Invalid choice. Using ${choice} selected at random`;
    span.log({ event: invalid });
  } else {
    span.log({ event: `Request contains valid choice ${choice}` });
  }
  return choice;
}

function getRandomPosition() {
  const randomNumber = Math.floor(Math.random() * Math.floor(38));
  return numToChoice[randomNumber];
}

function playRoulette(choice, parentSpan) {
  // Retrieve our previously-registered tracer
  const tracer = opentracing.globalTracer();
  const options = { childOf: parentSpan.context() };
  const span = tracer.startSpan('playRoulette', options);
  span.setTag('choice', choice);

  // Here we create a child span in parent function for direct access
  // within spinRouletteWheel().
  const childSpan = tracer.startSpan('spinRouletteWheel', { childOf: span.context() });
  const actual = spinRouletteWheel(childSpan);
  childSpan.finish();

  span.setTag('actual', actual);
  try {
    if (choice === actual) {
      throw new Error('Confirmation Bias!');
    }
    return `You Lost! The ball landed on ${actual}.`;
  } finally {
    span.finish();
  }
}

function spinRouletteWheel(span) {
  span.log({ event: 'Spinning Wheel.' });
  let position;
  for (let i = 0; i < 10000; i++) { // Simulate meaningful work
    position = getRandomPosition();
  }
  span.log({ event: 'Finished Spinning Wheel.' });
  return position;
}
