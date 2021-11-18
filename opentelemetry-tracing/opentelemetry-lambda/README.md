# Splunk OpenTelemetry Lambda Layer

This repository contains examples documenting usage of the Splunk OpenTelemetry Lambda Layer in all supported languages. 

Official Splunk documentation for can be found [here.](https://docs.splunk.com/Observability/gdi/get-data-in/serverless/aws/splunk-otel-lambda-layer.html#splunk-otel-lambda-layer)

For more detailed information about the layer and Splunk OpenTelemetry Lambda distribution visit:
- Layer: https://github.com/signalfx/lambda-layer-versions/tree/master/splunk-apm
- Distribution: https://github.com/signalfx/splunk-otel-lambda

## General instructions
All examples are accompanied with a SAM template, allowing quick and easy deployment to your AWS account. 
Additionally, the default configuration sends the data to Splunk Observability Cloud (`us0` realm).
 
Following placeholders needs to be set in each `template.yaml`:
- `REPLACE_WITH_SPLUNK_ACCESS_TOKEN` - your organization's access token
- `REPLACE_WITH_LAYER_ARN` - ARN of the newest Splunk OpenTelemetry Lambda Layer for your AWS Region, found under this 
URL: https://github.com/signalfx/lambda-layer-versions/blob/master/splunk-apm/splunk-apm.md 

After filling in these details simply run following command:
```
$ sam build && sam deploy --resolve-s3git
```

## Java
All examples can be found in [Java](./java).

Note: all example lambdas are string-based, so if testing from AWS console send simple string payload. 
API Gateway example needs to get an instance of `APIGatewayProxyRequestEvent`. Therefore, minimal AWS console 
payload should be as follows:
```
{
    body: "string payload"
}
```


