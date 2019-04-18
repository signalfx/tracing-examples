#!/bin/bash

set -ex 

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

aws --profile "${AWS_PROFILE}" --region "${AWS_DEFAULT_REGION}" \
    cloudformation deploy \
    --stack-name "${ENVIRONMENT_NAME}-mesh-resources" \
    --capabilities CAPABILITY_IAM \
    --template-file "${DIR}/ecommerce-mesh-resources.yaml"  \
    --parameter-overrides \
    EnvironmentName="${ENVIRONMENT_NAME}" \
    ServicesDomain="${SERVICES_DOMAIN}" \
    AppMeshName="${MESH_NAME}" \
    GatewayURL="${GATEWAY_URL}" \
    GatewayPort="${GATEWAY_PORT}" \
