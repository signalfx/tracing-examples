#!/bin/zsh

export OTEL_EXPORTER=zipkin
export OTEL_ZIPKIN_ENDPOINT=http://localhost:8888
export OTEL_ZIPKIN_SERVICE_NAME=wishlist-app

docker-compose -f postgres/docker-compose.yml up -d

mvn spring-boot:run -Dspring-boot.run.jvmArguments="-javaagent:/opt/opentelemetry-javaagent-all.jar"