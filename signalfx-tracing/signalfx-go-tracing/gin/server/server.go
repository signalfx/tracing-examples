package main

import (
	"fmt"

	"github.com/signalfx/signalfx-go-tracing/tracing"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/database"
	"github.com/signalfx/tracing-examples/signalfx-tracing/signalfx-go-tracing/gin/server/routes"
)

const (
	// ServiceName contains name of this service. This will show up on traces
	ServiceName = "signalfx-battleship"
	// TracingEndpoint contains a url to send traces
	TracingEndpoint = "http://localhost:9080/v1/trace"
	// Port that the app runs
	Port = 3030
	// MongoDriver is mongodb driver type. Selections are "mgo" | "mongo"
	MongoDriver = "mongo"
	// MongoHost is database hostname
	MongoHost = "localhost"
	// MongoPort is database port number
	MongoPort = 27017
	// MongoDbName is database name
	MongoDbName = "bships"
)

func init() {
	tracing.Start(tracing.WithEndpointURL(TracingEndpoint))
	database.InitManager(&database.Config{
		Driver:      MongoDriver,
		Host:        MongoHost,
		Port:        MongoPort,
		Name:        MongoDbName,
		ServiceName: ServiceName,
	})
}

func main() {
	router := routes.GetRouter(ServiceName)
	router.Run(fmt.Sprintf(":%d", Port))

	defer func() {
		tracing.Stop()
	}()
}
