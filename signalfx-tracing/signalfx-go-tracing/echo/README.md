# echo Instrumentation Example
This is an example of producing distributed traces using the [SignalFx Tracing Library for Go](https://github.com/signalfx/signalfx-go-tracing). This example provides an instrumented API server that serves a simple in-memory store exposing a CRUD API.

## Start Server
To run this example locally and send traces to your available Smart Agent or Gateway, please clone this repository and from this directory do the following:
```sh
$ cd server
$ go get
$ go run server.go
```

By default the service name is `simple-crud-api` and the tracing endpoint is `http://localhost:9080/v1/trace`. These values can be overridden by setting `ECHO_SERVICE_NAME` and `ECHO_TRACING_ENDPOINT` environment variables

## Generate Traces

The following operations can be performed to generate traces.

### Create a record

```sh
curl -X POST \                                                 
  -H 'Content-Type: application/json' \
  -d '{"record":{"name":"Joe Doe", "age" : 29}}' \
  localhost:1323/records
```

### Get a record
```sh
 curl localhost:1323/records/1 
```

### Update a record
```sh
 curl -X PUT \                                                  
  -H 'Content-Type: application/json' \
  -d '{"record":{"name":"Joe Doe", "age" : 28, "email" : "joedoe@gmail.com"}}' \
  localhost:1323/records/1
```

### Delete a record
```sh
 curl -XDELETE localhost:1323/records/1 
```