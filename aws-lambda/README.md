# Tracing AWS Lambda Functions

SignalFx accepts spans from instrumented code via HTTP, so it's possible to use properly configured
Jaeger tracers and the OpenTracing APIs for Go, JavaScript, Python, and Java to trace AWS Lambda Functions.
This directory provides demonstration implementations of a simple, instrumented Roulette app that operates
under the `RequestResponse` invocation type for the following runtimes:

- [Go 1.x](./jaeger-go)
- [Node.js 8.10](./jaeger-nodejs)
- [Python 2.7 and 3.6](./jaeger-python)
- [Java 8](./jaeger-java)


## Architecture

Each example in this section assumes the deployed Lambda function will be for synchronous handling of client
requests to an API Gateway endpoint.

```
    [Client Application]
          |      ↑     
          REST API 
          ↓      |
    [API Gateway Resource]
          |      ↑ 
      Trigger  Respond 
          ↓      |        
    [Your Lambda Function] — Spans via HTTP POST → [SignalFx Gateway]
```

If you are using asynchronous or non-`RequestResponse` invocation types, the examples can still serve
as a template for tracer instantiation and usage for reporting your traces to SignalFx within the
Lambda execution context.  However, the trigger [event](https://docs.aws.amazon.com/lambda/latest/dg/invoking-lambda-function.html)
will need to be updated to reflect your application's functionality.


## Deployment and Configuration

Each example provides a description of generating and configuring a Deployment Package for its respective
runtime.  All of these examples presume the setting of the `SIGNALFX_INGEST_URL` environment variable to
forward your traces to your deployed SignalFx Gateway or `SIGNALFX_ACCESS_TOKEN` for direct association
with your organization.  **Please note that bypassing the SignalFx Gateway is for demonstration purposes
only, and is not a supported deployment pattern.  The Gateway enables many powerful analytical features for
μAPM.** If neither the variables are set for your Lambda function via the AWS Console or CLI, all invocations
will generate an internal server error that will be logged to CloudWatch.

```
$ update-function-configuration \
  --function-name <MyTracedFunction> \
  --environment Variables={SIGNALFX_INGEST_URL=<MyGateway>}
```


## Resources

- [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [AWS Lambda Execution Model](https://docs.aws.amazon.com/lambda/latest/dg/running-lambda-code.html)
- [Deploying Lambda-based Applications](https://docs.aws.amazon.com/lambda/latest/dg/deploying-lambda-apps.html)
- [Programming Model](https://docs.aws.amazon.com/lambda/latest/dg/programming-model-v2.html)
- [Creating a Deployment Package](https://docs.aws.amazon.com/lambda/latest/dg/deployment-package-v2.html)
- [Best Practices for Working with AWS Lambda Functions](https://docs.aws.amazon.com/lambda/latest/dg/best-practices.html)
