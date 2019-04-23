#!/bin/bash

if [ -z $DOCKER_REPO ]; then
    echo "Please set the environment variable DOCKER_REPO."
    exit 1
fi

docker build -t ecommerce-requestgen:latest .
docker tag ecommerce-requestgen:latest ${DOCKER_REPO}:requestgen
docker push ${DOCKER_REPO}:requestgen
