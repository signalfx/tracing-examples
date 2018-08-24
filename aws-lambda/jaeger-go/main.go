// This Lambda function is an example of an OpenTracing-instrumented Roulette
// game using the Jaeger Go tracer where requests containing a chosen number made
// to an API Gateway resource are compared to a random number from the wheel.
//
// GET /my_resource/?choice=36 -> 404 Loss
// GET /my_resource/?choice=00 -> 200 Win
//
// Winning requests will produce a logged error.
//
package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"math/rand"
	"os"
	"strconv"
	"time"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-lambda-go/lambdacontext"
	opentracing "github.com/opentracing/opentracing-go"
	"github.com/opentracing/opentracing-go/ext"
	"github.com/uber/jaeger-client-go"
	jaegercfg "github.com/uber/jaeger-client-go/config"
	"github.com/uber/jaeger-client-go/transport"
)

// Wheel position to address mappings, helpful for "00"
var choiceToNum map[string]int
var numToChoice [38]string

func init() {
	choiceToNum = map[string]int{"00": 37}
	for i := 0; i < 37; i++ {
		k := strconv.Itoa(i)
		choiceToNum[k] = i
	}

	for k, v := range choiceToNum {
		numToChoice[v] = k
	}
}

type Body struct {
	TraceId string
	Result  string
	Choice  string
}

func main() {
	lambda.Start(handleRequest)
}

func handleRequest(ctx context.Context, request events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {
    // Create a tracer for the lifetime of the request
	tracer, tracerCloser := createTracer()
	defer tracerCloser.Close() // We always want to initiate a span flush on exit

	// Create the root span, specifying a span.kind tag in the process
	// https://github.com/opentracing/opentracing-go/blob/master/ext/tags.go
	rootSpan := tracer.StartSpan("RequestHandler", ext.SpanKindRPCServer)
	defer rootSpan.Finish() // We always want to close spans at end of execution

	// Collect the current trace ID for response body to facilitate querying
	traceId := getSpanTraceId(rootSpan.Context())
	rootSpan.SetTag("TraceID", traceId)

	// Obtain Lambda-provided context information and tag span for
	// debugging and analytics
	// https://github.com/aws/aws-lambda-go/blob/master/lambdacontext/context.go
	lc, _ := lambdacontext.FromContext(ctx)
	rootSpan.SetTag("AwsRequestID", lc.AwsRequestID)

	// Retrieve the choice parameter from client request, passing the span
	// for tagging and logging within same context.
	choice := getChoice(request, rootSpan)

	// Invoke Lambda business logic, providing an opentracing.SpanContext for
	// establishing span references
	result, statusCode := playRoulette(choice, rootSpan.Context())

	rootSpan.SetTag("result", result)
	rootSpan.SetTag("statusCode", statusCode)

	body := Body{traceId, result, choice}
	_responseBody, err := json.Marshal(body)
	if err != nil {
		panic(err)
	}
	responseBody := string(_responseBody[:])

	return events.APIGatewayProxyResponse{Body: responseBody, StatusCode: statusCode}, nil
}

func createTracer() (opentracing.Tracer, io.Closer) {
	accessToken := os.Getenv("SIGNALFX_ACCESS_TOKEN")
	if accessToken == "" {
		// Raised panic contexts will be logged to CloudWatch
		panic(errors.New("You must set the SIGNALFX_ACCESS_TOKEN Lambda environment variable to be your token."))
	}
	const ingestUrl = "https://ingest.signalfx.com/v1/trace"

	// This creates a configuration struct for Jaeger based on environment variables.
	// See https://github.com/jaegertracing/jaeger-client-go/blob/master/README.md#environment-variables
	cfg, err := jaegercfg.FromEnv()
	if err != nil {
		// parsing errors might happen here, such as when we get a string where we expect a number
		panic(err)
	}

	// Create a Jaeger HTTP Thrift transport, pointing it to our trace endpoint
	// which accepts thrift encoded spans.
	transport := transport.NewHTTPTransport(ingestUrl, transport.HTTPBasicAuth("auth", accessToken))

	// Here we override the service name for the tracer for this example.  This
	// would otherwise be set from the env var JAEGER_SERVICE_NAME.
	cfg.ServiceName = "signalfx-lambda-go-example"

	// Here we are configuring span sampling so that all spans are sampled.
	// For large applications this is probably not feasible.
	// See https://github.com/jaegertracing/jaeger-client-go/blob/master/README.md#sampling
	cfg.Sampler = &jaegercfg.SamplerConfig{
		Type:  "const",
		Param: 1,
	}

	// This creates the Jaeger tracer from the configuration, using our Thrift HTTP transport.
	tracer, closer, err := cfg.NewTracer(jaegercfg.Reporter(jaeger.NewRemoteReporter(transport)))
	if err != nil {
		panic(errors.New(fmt.Sprintf("Could not initialize jaeger tracer: %v", err.Error())))
	}

	// Set the tracer as the global tracer in case you want to start a span
	// without having a direct reference to the tracer.
	opentracing.SetGlobalTracer(tracer)

	return tracer, closer
}

// Per OpenTracing, trace IDs are implementation specific, so this
// Jaeger interface is not intended for instrumentation and is for
// ease of demo trace retrieval only.
func getSpanTraceId(ctx opentracing.SpanContext) string {
	jaegerCtx := ctx.(jaeger.SpanContext)
	low := jaegerCtx.TraceID().Low
	return fmt.Sprintf("%016x", low)
}

// getChoice retrieves a user's spin choice from an API Gateway request.
// If none or an invalid choice is provided in the request, it selects one at random.
func getChoice(request events.APIGatewayProxyRequest, span opentracing.Span) (choice string) {
	choiceQP, qpOk := request.QueryStringParameters["choice"]
	if !qpOk {
		span.LogKV("event", "No choice query parameter provided.")
	} else {
		span.SetTag("choiceQueryParameter", choiceQP)
	}
	_, validChoice := choiceToNum[choiceQP]
	if !validChoice {
		randomChoice := numToChoice[getRandomNumber()]
		invalid := fmt.Sprintf("Invalid choice query parameter \"%s\".  Using %s selected at random.",
			choiceQP, randomChoice)
		span.LogKV("event", invalid)
		span.SetTag("randomChoice", randomChoice)
		choice = randomChoice
	} else {
		choice = choiceQP
	}
	return choice
}

func playRoulette(choice string, parentCtx opentracing.SpanContext) (result string, statusCode int) {
	// Retrieve previously initialized Tracer
	tracer := opentracing.GlobalTracer()

	// Create a child span by using opentracing.ChildOf reference option
	// https://github.com/opentracing/opentracing-go/blob/bd9c3193394760d98b2fa6ebb2291f0cd1d06a7d/tracer.go#L237
	span := tracer.StartSpan("playRoulette", opentracing.ChildOf(parentCtx))
	defer span.Finish() // Always close created spans

	span.SetTag("choice", choice)

	actual := spinRouletteWheel(span.Context())
	span.SetTag("actual", actual)

	// Here we defer a recover function to handle panic events and override playRoulette' named return values
	// upon winning Roulette spins.
	defer func() {
		if r := recover(); r != nil {
			// Mark span as having panicked using OpenTracing.Tag helper
			ext.Error.Set(span, true)
			span.LogKV("error", r) // Log the error event and panic value
			statusCode = 200
			result = "You Won!"
		}
	}()

	if actual == choice {
		panic(errors.New("Confirmation Bias!"))
	}
	statusCode = 404
	result = fmt.Sprintf("You Lost! The ball landed on %s.", actual)
	return result, statusCode
}

func spinRouletteWheel(parentCtx opentracing.SpanContext) (actual string) {
	tracer := opentracing.GlobalTracer()

	childSpan := tracer.StartSpan("SpinRouletteWheel", opentracing.ChildOf(parentCtx))
	defer childSpan.Finish()

	// Log events (key/value pairs) to record the time of event occurences
	childSpan.LogKV("event", "Spinning Wheel")
	for i := 0; i < 1000; i++ { // simulate meaningful work
		position := getRandomNumber()
		actual = numToChoice[position]
	}
	childSpan.LogKV("event", "Finished Spinning Wheel")
	childSpan.SetTag("position", actual)
	return actual
}

func getRandomNumber() int {
	rand.Seed(time.Now().UnixNano())
	random := rand.Intn(38)
	return random
}
