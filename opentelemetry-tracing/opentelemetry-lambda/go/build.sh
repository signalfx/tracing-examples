#!/bin/bash -e

mkdir -p build
GOOS=linux GOARCH=${GOARCH-amd64} go build -o ./build/bootstrap .
cd build || exit
zip bootstrap.zip bootstrap
