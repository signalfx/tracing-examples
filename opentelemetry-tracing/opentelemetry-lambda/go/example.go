package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-lambda-go/lambda"
	"github.com/signalfx/splunk-otel-go/distro"
	"go.opentelemetry.io/contrib/instrumentation/github.com/aws/aws-lambda-go/otellambda"
	"go.opentelemetry.io/otel"
)

func main() {
	distro.Run()
	flusher := otel.GetTracerProvider().(otellambda.Flusher)
	lambda.Start(otellambda.InstrumentHandler(HandleRequest, otellambda.WithFlusher(flusher)))
}

type MyEvent struct {
	Name string `json:"name"`
}

func HandleRequest(ctx context.Context, name MyEvent) (string, error) {
	return fmt.Sprintf("Hello %s!", name.Name), nil
}
