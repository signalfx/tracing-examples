#!/bin/zsh

export OTEL_EXPORTER=zipkin
export OTEL_ZIPKIN_ENDPOINT=http://localhost:9080/v1/trace
export OTEL_ZIPKIN_SERVICE_NAME=my-java-app

docker run -d --name redis-tracing-test -p 6379:6379 redis
mvn package
java  -javaagent:/opt/opentelemetry-javaagent-all.jar \
      -jar target/java-agent-example-1.0-SNAPSHOT-shaded.jar https://google.com