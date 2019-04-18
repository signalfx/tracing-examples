#!/bin/bash

docker build ./ -t ecommerce-envoy-config:latest --build-arg ENDPOINT_URL=${SIGNALFX_ENDPOINT_URL}
docker tag ecommerce-envoy-config:latest ${DOCKER_REPO}:config
docker push ${DOCKER_REPO}:config
