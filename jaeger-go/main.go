// This is a simple program that estimates the value of Pi using Leibniz's
// formula.  It uses separate goroutines to calculate sections of the estimate
// and then combines them together in the main goroutine.
package main

import (
	"fmt"
	"io"
	"log"

	opentracing "github.com/opentracing/opentracing-go"
	jaegercfg "github.com/uber/jaeger-client-go/config"
)

// Constants used by the demo application logic
const (
	numParts  = 3
	partTerms = 100000000
)

func main() {
	// Create our tracer.  This should only have to be done once per
	// application.
	tracer, tracerCloser, err := createTracer()
	if err != nil {
		log.Fatal("Could not create Jaeger tracer", err)
	}

	// Create an initial span that will trace our application run.  We are
	// calling it "root" but the name can be anything.  StartSpan both creates
	// and starts the span.
	//
	// Here we are using the tracer by an explicit reference to it, but in more
	// complex applications it will be must easier to rely on the global tracer
	// instance, in which case this line would be:
	// parentSpan := opentracing.StartSpan("root")
	parentSpan := tracer.StartSpan("root")

	// Set a tag on the newly created span.  This would be more significant if
	// the value were dynamic.
	parentSpan.SetTag("totalTerms", numParts*partTerms)

	results := make(chan float64)
	for i := 0; i < numParts; i++ {
		part := i
		// Do some work in separate goroutines
		go func() {
			// This creates a child span of the parent span.  The parent span
			// must be explicitly given.  In more complex applications, the
			// parent span can be passed around within a context.Context instance
			// (see https://github.com/opentracing/opentracing-go#creating-a-span-given-an-existing-go-contextcontext)
			sp := tracer.StartSpan("calculatePart", opentracing.ChildOf(parentSpan.Context()))
			// We must always explicitly finish a span.
			defer sp.Finish()

			// Set a tag to uniquely identify goroutines
			sp.SetTag("part", part)

			termStart := part * partTerms
			results <- calculatePart(termStart, termStart+partTerms)
		}()
	}

	partsRemaining := numParts
	var piEstimate float64
	for partsRemaining > 0 {
		select {
		case part := <-results:
			piEstimate += part * 4.0
			partsRemaining--
		}
	}

	fmt.Printf("pi ~= %#v\n", piEstimate)

	// Now that our application is done, we must also close the parent span.
	parentSpan.Finish()

	// It is important to explicitly close the tracer when the application
	// shuts down to prevent spans from being lost.
	tracerCloser.Close()
}

// This function is used by the application logic and contains no tracing
// code.
func calculatePart(termStart int, termEnd int) float64 {
	var partialSum float64
	for k := termStart; k < termEnd; k++ {
		numerator := 1.0
		if k%2 != 0 {
			numerator = -1.0
		}
		partialSum += numerator / float64(((2 * k) + 1))
	}
	return partialSum
}

func createTracer() (opentracing.Tracer, io.Closer, error) {
	// This creates a configuration struct for Jaeger based on environment
	// variables. See https://github.com/jaegertracing/jaeger-client-go/blob/master/README.md#environment-variables
	// The rest of this setup expects that JAEGER_ENDPOINT and JAEGER_PASSWORD
	// have already been set on the application's environment.
	cfg, err := jaegercfg.FromEnv()
	if err != nil {
		// parsing errors might happen here, such as when we get a string where we expect a number
		return nil, nil, err
	}

	// Here we set a default service name for the tracer for this example.  This
	// would already be set if you set the JAEGER_SERVICE_NAME env var.
	if cfg.ServiceName == "" {
		cfg.ServiceName = "signalfx-jaeger-go-example"
	}

	// Here we are configuring span sampling so that all spans are sampled and
	// sent. Our standard deployment model sends all spans from the application
	// to the Smart Agent and Gateway.
	cfg.Sampler = &jaegercfg.SamplerConfig{
		Type:  "const",
		Param: 1,
	}

	// This creates the Jaeger tracer from the configuration, using our Thrift
	// HTTP transport.
	tracer, closer, err := cfg.NewTracer()
	if err != nil {
		return nil, nil, err
	}

	// Set the tracer as the global tracer in case you want to start a span
	// without having a direct reference to the tracer.  The use of the global
	// tracer actually appears to be the best-practice in Golang but we show an
	// explicit invocation of the tracer in the example above to make it more
	// obvious what is going on.
	opentracing.SetGlobalTracer(tracer)

	return tracer, closer, nil
}
