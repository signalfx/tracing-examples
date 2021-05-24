#!/bin/zsh

# Build demo application
./mvnw package

# Download instrumentation agent if does not exist yet
curl -sSL -C - -o splunk-otel-javaagent-all.jar 'https://github.com/signalfx/splunk-otel-java/releases/latest/download/splunk-otel-javaagent-all.jar'

# Provide recommended configuration for the instrumentation agent
export OTEL_RESOURCE_ATTRIBUTES="service.name=my-sample-app"

# Start a Redis container
docker run --rm -d -p 6379:6379 --name redis redis

# Start OpenTelemetry Collector
docker run --rm -d -p 4317:4317 --name collector otel/opentelemetry-collector

# Add instrumentation agent to the JVM running your application with `-javaagent` option
# and execute your application
java  -javaagent:splunk-otel-javaagent-all.jar \
      -jar target/java-agent-example-1.0-SNAPSHOT-shaded.jar https://google.com

# Stop and remove Redis container
docker stop redis
