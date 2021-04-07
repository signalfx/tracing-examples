#!/bin/zsh

export OTEL_RESOURCE_ATTRIBUTES=service.name=wishlist-app

## use below to point to a default Jaeger installation on localhost:
#export OTEL_EXPORTER_JAEGER_ENDPOINT=http://localhost:14268/api/traces

docker-compose -f postgres/docker-compose.yml up -d

mvn spring-boot:run -Dspring-boot.run.jvmArguments="-javaagent:splunk-otel-javaagent-all.jar"