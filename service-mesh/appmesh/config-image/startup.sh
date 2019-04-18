#!/bin/sh

set -ex

export HOST_IP=$(curl http://169.254.169.254/latest/meta-data/local-ipv4)

echo $HOST_IP
echo $1

endpoint_url=$(echo $1 | sed ' s/\/\///g; s/POD_HOST_IP/'"${HOST_IP}"'/g' | awk '{split($0,a,":"); print a[2]}')
endpoint_port=$(echo $1 | awk '{split($0,a,":"); print a[3]}')

sed -i 's/SIGNALFX_ENDPOINT_URL/'"${endpoint_url}"'/g; s/SIGNALFX_ENDPOINT_PORT/'"${endpoint_port}"'/g' /envoy_config/tracing.yaml

cat /envoy_config/tracing.yaml
