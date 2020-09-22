#!/bin/zsh

export OTEL_ZIPKIN_SERVICE_NAME=my-sample-app

docker run -d --name redis-tracing-test -p 6379:6379 redis
mvn package
java  -javaagent:/opt/opentelemetry-javaagent-all.jar \
      -jar target/java-agent-example-1.0-SNAPSHOT-shaded.jar https://google.com