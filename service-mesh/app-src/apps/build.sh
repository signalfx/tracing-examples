#!/bin/bash

if [ -z $1 ]; then
    docker_repo=$DOCKER_REPO
else
    docker_repo=$1
fi

apps=(api cart catalog checkout payment refresh)

for app in "${apps[@]}"
do
   :
   docker build -t "ecommerce-${app}" --build-arg APP=${app} .

   docker tag ecommerce-${app}:latest ${docker_repo}:${app}
   docker push ${docker_repo}:${app}
done

