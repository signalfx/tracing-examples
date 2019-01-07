# OpenTracing in AWS Lambda Ruby 2.5

This is an example Lambda function that shows how to use the Ruby Jaeger client to send spans to SignalFx.

The code can be seen in [prime_sum.rb](./prime_sum.rb).

## Using a Deployment Package

A Ruby deployment package has your project's Ruby files and it's gem dependencies.

### Downloading dependencies

The dependencies for this example have already been provided in this repo, so
the next steps are not necessary unless starting from scratch.

The deployment's `Gemfile` dependencies must be downloaded locally.

```bash
$ bundle install --path vendor/bundle
```

This part is only needed if any dependencies have native extensions. The native
extensions must be built on the same platform as the Lambda runtime. The easiest
way to acheive this is by building inside a Docker container:

```bash
$ docker run -v `pwd`:`pwd` -w `pwd` -i -t lambci/lambda:build-ruby2.5 bundle install --deployment
```

### Create and deploy package

To build the deployment package:

```bash
$ zip -r ruby_lambda_example.zip prime_sum.rb vendor
```

This zip file can then be uploaded to the AWS Console.

The handler should be registered as `prime_sum.handle_request`. Environment
variables `SIGNALFX_ACCESS_TOKEN`, `SIGNALFX_INGEST_URL`, and
`SIGNALFX_SERVICE_NAME` must also be set in the console.

A test payload can be used to run the application:

```json
{
    'body': 'some message body to be tagged'
    'queryStringParameters': {
        'limit': 100
    }
}
```

## References

- [Building Lambda Functions with Ruby]( https://docs.aws.amazon.com/lambda/latest/dg/lambda-ruby.html )
- [AWS Lambda Deployment Package in Ruby]( https://docs.aws.amazon.com/lambda/latest/dg/ruby-package.html )
