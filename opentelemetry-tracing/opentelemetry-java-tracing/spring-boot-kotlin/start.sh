#!/bin/zsh

# Start OpenTelemetry Collector
docker run --rm -d -p 4317:4317 --name collector otel/opentelemetry-collector

# Download instrumentation agent if does not exist yet
curl -sSL -C - -o splunk-otel-javaagent-all.jar 'https://github.com/signalfx/splunk-otel-java/releases/latest/download/splunk-otel-javaagent-all.jar'

# Provide recommended configuration for the instrumentation agent
export OTEL_RESOURCE_ATTRIBUTES="service.name=wishlist-app"

# Run database for the application
docker run --name postgres -p 5432:5432 --rm -d -e POSTGRES_PASSWORD=password postgres:latest

# Stop and remove all containers on exit
trap "docker stop postgres collector" SIGINT

# Run demo application adding instrumentation agent to the JVM startup
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-javaagent:splunk-otel-javaagent-all.jar"