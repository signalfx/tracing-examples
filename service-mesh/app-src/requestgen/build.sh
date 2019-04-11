#!/bin/bash

if [ -z $1 ]; then
    docker_repo=$DOCKER_REPO
else
    docker_repo=$1
fi

docker build -t ecommerce-requestgen:latest .
docker tag ecommerce-requestgen:latest ${docker_repo}:requestgen
docker push ${docker_repo}:requestgen
