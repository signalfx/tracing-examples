package main

import (
	"crypto/tls"
	"fmt"
	"context"
	"go.opentelemetry.io/otel"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/signalfx/splunk-otel-go/distro"
	"go.opentelemetry.io/contrib/instrumentation/github.com/aws/aws-lambda-go/otellambda"
)

type MyEvent struct {
	Name string `json:"name"`
}

func HandleRequest(ctx context.Context, name MyEvent) (string, error) {
	return fmt.Sprintf("Hello %s!", name.Name ), nil
}

func clientTLSConfig() *tls.Config {
	return &tls.Config{
		MinVersion: tls.VersionTLS13,
	}
}

func main() {

	distro.Run(distro.WithTLSConfig(clientTLSConfig()))
	flusher := otel.GetTracerProvider().(otellambda.Flusher)

	lambda.Start(otellambda.InstrumentHandler(HandleRequest, otellambda.WithFlusher(flusher)))
}