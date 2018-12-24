# OpenTracing and Jaeger in AWS Lambda Go 1.x Runtime

This is an example of a simple, traced Lambda function using the Jaeger Go tracer with SignalFx.
See [main.go](./main.go) for the example code.

## Building and Creating a Deployment Package

A Go deployment package for AWS Lambda is a .zip file consisting of your built project's executable.
To build this application and create the deployable archive follow these instructions from the root
`tracing-examples` directory:

```
$ pwd  # Confirm this is <your_local_path/tracing-examples>
$ mkdir -p $GOPATH/src/github.com/signalfx
$ ln -s $(pwd) $GOPATH/src/github.com/signalfx/tracing-examples
$ cd $GOPATH/src/github.com/signalfx/tracing-examples/aws-lambda/jaeger-go
$ GOOS=linux go build .  # GOOS=linux is needed for the Lambda runtime environment
$ zip my_traced_golang_lambda.zip ./jaeger-go
```

The resulting `my_traced_golang_lambda.zip` can be uploaded to S3 or in your browser via the AWS Console
during function creation. Register your handler as `jaeger-go` and don't forget to set the
`SIGNALFX_INGEST_URL` environment variable to point to your Gateway.  You should be able test the application
with the following test event:

```
{
  "queryStringParameters": {
    "choice": "00"
  }
}
```

The execution result should succeed with a response similar to:
```
{
  "statusCode": 404,
  "body": "{\"TraceId\":\"2296bd21606fc2e4\",\"Result\":\"You Lost! The ball landed on 35.\",\"Choice\":\"00\"}"
}
```

## Resources

- [Programming Model for Authoring Lambda Functions in Go](https://docs.aws.amazon.com/lambda/latest/dg/go-programming-model.html)
- [Creating a Deployment Package (Go)](https://docs.aws.amazon.com/lambda/latest/dg/lambda-go-how-to-create-deployment-package.html)
