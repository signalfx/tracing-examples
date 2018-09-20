package main

import (
	"fmt"
	"math/rand"
	"net/http"
	"time"

	"golang.org/x/net/context"

	"go.opencensus.io/exporter/jaeger"
	"go.opencensus.io/trace"
)

const (

	// the HTTP thrift endpoint where the spans will be sent
	ingestUrl = "https://ingest.signalfx.com/v1/trace"

	// the service name to export these spans with
	serviceName = "test-go-jaeger"

	// please provide a valid access token
	accessToken = ""
)

var (
	span   *trace.Span
	random *rand.Rand
	count  int
)

func main() {

	source := rand.NewSource(time.Now().UnixNano())
	random = rand.New(source)

	initExporter(serviceName, ingestUrl, accessToken)

	startServer()
}

func initExporter(serviceName, ingestUrl, accessToken string) {

	// create the jaeger exporter
	exporter, err := jaeger.NewExporter(jaeger.Options{
		CollectorEndpoint: ingestUrl,
		Process: jaeger.Process{
			ServiceName: serviceName,
		},
		Username: "auth",
		Password: accessToken,
	})
	if err != nil {
		fmt.Println("Failed to initialize exporter: ", err)
		return
	}

	// this will flush out any pending spans
	defer exporter.Flush()

	// register the exporter and configure the tracer to always sample
	trace.RegisterExporter(exporter)
	trace.ApplyConfig(trace.Config{DefaultSampler: trace.AlwaysSample()})
}

func startServer() *http.Server {

	// start a simple http server listening at localhost:8080/test
	srv := &http.Server{Addr: ":8080"}
	http.HandleFunc("/test", handle)
	srv.ListenAndServe()
	return srv
}

func handle(w http.ResponseWriter, r *http.Request) {

	// this will be a parent span, so create a new blank context
	ctx := context.Background()

	// start the span and defer closing it until we leave the "handle" function
	ctx, span := trace.StartSpan(ctx, "handle")
	defer span.End()

	count = 0
	randInt := random.Intn(100)
	fmt.Println("Random number: ", randInt)

	for count < randInt {

		// since we want to register the spans for the "increment" function
		// as child spans of this one, we need to pass in the context
		increment(ctx)
	}

	fmt.Fprint(w, randInt)
}

func increment(ctx context.Context) {

	// instead of creating a new empty context, use the context passed in
	// from the parent span
	ctx, span := trace.StartSpan(ctx, "increment")
	defer span.End()

	count++
}
