package main

import (
	echotrace "github.com/signalfx/signalfx-go-tracing/contrib/labstack/echo"
	"github.com/signalfx/signalfx-go-tracing/tracing"
	"net/http"
	"os"
	"strconv"

	"github.com/labstack/echo"
)

const (
	// ServiceName contains name of this service. This will show up on traces
	DefaultServiceName = "simple-crud-api"
	// TracingEndpoint contains a url to send traces
	DefaultTracingEndpoint = "http://localhost:9080/v1/trace"
)

type (
	entry struct {
		ID     int         `json:"id"`
		Record interface{} `json:"record"`
	}
)

var (
	records = map[int]*entry{}
	seq     = 1
)

// Creates a new record
func createRecord(c echo.Context) error {
	u := &entry{
		ID: seq,
	}

	err := c.Bind(u)

	if err != nil {
		return c.JSON(http.StatusBadRequest, "Invalid input")
	}
	records[u.ID] = u
	seq++
	return c.JSON(http.StatusCreated, u)
}

// Gets a record corresponding to the id
func getRecord(c echo.Context) error {
	id, _ := strconv.Atoi(c.Param("id"))
	if records[id] == nil {
		return c.JSON(http.StatusNotFound, "Record not found")
	}
	return c.JSON(http.StatusOK, records[id])
}

// Updates the record corresponding to the id
func updateRecord(c echo.Context) error {
	u := new(entry)
	if err := c.Bind(u); err != nil {
		return err
	}
	id, _ := strconv.Atoi(c.Param("id"))
	if records[id] == nil {
		return c.JSON(http.StatusNotFound, "Record not found")
	}
	records[id].Record = u.Record
	return c.JSON(http.StatusOK, records[id])
}

// Deletes the record corresponding to the id
func deleteRecord(c echo.Context) error {
	id, _ := strconv.Atoi(c.Param("id"))
	delete(records, id)
	return c.NoContent(http.StatusNoContent)
}

func main() {
	tracingEndpoint := os.Getenv("ECHO_TRACING_ENDPOINT")
	if tracingEndpoint == "" {
		tracingEndpoint = DefaultTracingEndpoint
	}

	serviceName := os.Getenv("ECHO_SERVICE_NAME")
	if serviceName == "" {
		serviceName = DefaultServiceName
	}

	tracing.Start(tracing.WithEndpointURL(tracingEndpoint), tracing.WithServiceName(serviceName))
	defer tracing.Stop()

	e := echo.New()
	e.Use(echotrace.Middleware())

	// Routes
	e.POST("/records", createRecord)
	e.GET("/records/:id", getRecord)
	e.PUT("/records/:id", updateRecord)
	e.DELETE("/records/:id", deleteRecord)

	// Start server
	e.Logger.Fatal(e.Start(":1323"))
}

