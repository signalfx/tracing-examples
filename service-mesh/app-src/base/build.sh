#!/bin/bash

git clone git+ssh://git@github.com/signalfx/signalfx-python-tracing

docker build -t ecommerce-base:latest .

rm -rf signalfx-python-tracing
