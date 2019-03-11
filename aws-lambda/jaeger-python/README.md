# OpenTracing and Jaeger in AWS Lambda Python Runtime

This is an example of a simple, traced Lambda function using the SignalFx Lambda Wrapper and Jaeger Python tracer with SignalFx.
See [example.py](./example.py) for the example code.

## Building and creating a Deployment Package

A Python deployment package for AWS Lambda is a .zip file consisting of your application code and any dependencies.
To prepare this project and create the deployable archive follow these instructions from this document's parent
directory:

```
$ pwd  # Confirm this is <your_local_path/tracing-examples/aws-lambda/jaeger-python>
$ pip install -r requirements.txt -t .
$ find . -name "*.py[co]" -delete
$ find . -name "*.dist-info" -exec rm -r "{}" \;
$ # If you are running your Lambda function in the Python 3.6 runtime environment
$ # you'll need to remove the concurrent package as its code is evaluated at load time
$ # and raises a SyntaxError for using the obsoleted raise Exception tuple syntax.  This
$ # is part of the standard library for Python 3.2+ so is not necessary.
$ # rm -rf ./concurrent
$ zip -r my_traced_python_lambda.zip *
```

The resulting `my_traced_python_lambda.zip` can be uploaded to S3 or in your browser via the AWS Console
during function creation. Register your handler as `example.request_handler` and don't forget to set the
`SIGNALFX_TRACING_URL` environment variable to point to your Gateway. Set
`SIGNALFX_SERVICE_NAME` to `signalfx-lambda-python-example` or something else
descriptive. You should be able test the application with the following test
event:

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
  "body": "{\"result\": \"You lost! The ball landed on 18.\", \"trace_id\": \"6527113c86f90ce9\", \"choice\": \"00\"}",
  "statusCode": 404
}
```

## Resources

- [Programming Model for Authoring Lambda Functions in Python](https://docs.aws.amazon.com/lambda/latest/dg/python-programming-model.html)
- [Creating a Deployment Package (Python)](https://docs.aws.amazon.com/lambda/latest/dg/lambda-python-how-to-create-deployment-package.html)
