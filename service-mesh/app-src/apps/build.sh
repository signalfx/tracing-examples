#!/bin/bash

if [ -z $DOCKER_REPO ]; then
    echo "Please set the environment variable DOCKER_REPO."
    exit 1
fi

apps=(api cart catalog checkout payment refresh)

for app in "${apps[@]}"
do
   :
   docker build -t "ecommerce-${app}" --build-arg APP=${app} .

   docker tag ecommerce-${app}:latest ${DOCKER_REPO}:${app}
   docker push ${DOCKER_REPO}:${app}
done

