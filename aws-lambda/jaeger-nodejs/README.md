# OpenTracing and Jaeger in AWS Lambda Node.js 8.10 Runtime

This is an example of a simple, traced Lambda function using the Jaeger Node.js tracer with SignalFx.
See [index.js](./index.js) for the example code.

## Building and creating a Deployment Package

A Node.js deployment package for AWS Lambda is a .zip file consisting of your project code and any dependencies.
To prepare this project and create the deployable archive follow these instructions from this document's parent
directory:

```
$ pwd  # Confirm this is <your_local_path/tracing-examples/aws-lambda/jaeger-nodejs>
$ npm install
$ zip -r my_traced_node_lambda.zip *
```

The resulting `my_traced_node_lambda.zip` can be uploaded to S3 or in your browser via the AWS Console
during function creation. Register your handler as `index.requestHandler` and don't forget to set the
`SIGNALFX_ACCESS_TOKEN` environment variable.  You should be able test the application with
the following test event:

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
  "body": "{\"traceId\":\"836e0d4cb9691447\",\"result\":\"You Lost! The ball landed on 29.\",\"choice\":\"00\"}"
}
```

## Resources

- [Programming Model (Node.js)](https://docs.aws.amazon.com/lambda/latest/dg/programming-model.html)
- [Creating a Deployment Package (Node.js)](https://docs.aws.amazon.com/lambda/latest/dg/nodejs-create-deployment-pkg.html)
