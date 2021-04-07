#!/bin/zsh

export OTEL_RESOURCE_ATTRIBUTES=service.name=my-sample-app
export OTEL_TRACES_EXPORTER=otlp

echo Starting a redis container
docker run -d -p 6379:6379 redis

# wait for redis to be up and running
echo Waiting for redis to be ready
sleep 3

mvn package
java  -javaagent:splunk-otel-javaagent-all.jar \
      -jar target/java-agent-example-1.0-SNAPSHOT-shaded.jar https://google.com
