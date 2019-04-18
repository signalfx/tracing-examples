#!/bin/bash

# set -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

stack_output=$(aws --profile "${AWS_PROFILE}" --region "${AWS_DEFAULT_REGION}" \
    cloudformation describe-stacks --stack-name "${ENVIRONMENT_NAME}-ecs-cluster" \
    | jq '.Stacks[].Outputs[]')

task_role_arn=($(echo $stack_output \
    | jq -r 'select(.OutputKey == "TaskIamRoleArn") | .OutputValue'))

execution_role_arn=($(echo $stack_output \
    | jq -r 'select(.OutputKey == "TaskExecutionIamRoleArn") | .OutputValue'))

ecs_service_log_group=($(echo $stack_output \
    | jq -r 'select(.OutputKey == "ECSServiceLogGroup") | .OutputValue'))

envoy_log_level="debug"

# Api Task Definition
envoy_container_json=$(jq -n \
    --arg ENVOY_SERVICE_NAME "api" \
    --arg ENVOY_IMAGE $ENVOY_IMAGE \
    --arg VIRTUAL_NODE "mesh/$MESH_NAME/virtualNode/api-vn" \
    --arg APPMESH_XDS_ENDPOINT "${APPMESH_XDS_ENDPOINT}" \
    --arg ENVOY_LOG_LEVEL $envoy_log_level \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg AWS_LOG_STREAM_PREFIX_ENVOY "api-envoy" \
    -f "${DIR}/envoy-container.json")
task_def_json=$(jq -n \
    --arg CONFIG_IMAGE "${DOCKER_REPO}:config" \
    --arg NAME "$ENVIRONMENT_NAME-Api" \
    --arg APP_IMAGE "${DOCKER_REPO}:api" \
    --arg APP_PORT 5000 \
    --arg CART_SERVICE "cart.${SERVICES_DOMAIN}:5001" \
    --arg CATALOG_SERVICE "catalog.${SERVICES_DOMAIN}:5004" \
    --arg CONFIG_VOLUME "${DOCKER_REPO}:config" \
    --arg SIGNALFX_ENDPOINT_URL "${SIGNALFX_ENDPOINT_URL}" \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_LOG_STREAM_PREFIX_APP "api-app" \
    --arg TASK_ROLE_ARN $task_role_arn \
    --arg EXECUTION_ROLE_ARN $execution_role_arn \
    --argjson ENVOY_CONTAINER_JSON "${envoy_container_json}" \
    -f "${DIR}/api-task.json")
task_def=$(aws --profile "${AWS_PROFILE}" --region "${AWS_DEFAULT_REGION}" \
    ecs register-task-definition \
    --cli-input-json "$task_def_json")
api_task_def_arn=($(echo $task_def \
    | jq -r '.taskDefinition | .taskDefinitionArn'))

# Cart Task Definition
envoy_container_json=$(jq -n \
    --arg ENVOY_SERVICE_NAME "cart" \
    --arg ENVOY_IMAGE $ENVOY_IMAGE \
    --arg VIRTUAL_NODE "mesh/$MESH_NAME/virtualNode/cart-vn" \
    --arg APPMESH_XDS_ENDPOINT "${APPMESH_XDS_ENDPOINT}" \
    --arg ENVOY_LOG_LEVEL $envoy_log_level \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg AWS_LOG_STREAM_PREFIX_ENVOY "cart-envoy" \
    -f "${DIR}/envoy-container.json")
task_def_json=$(jq -n \
    --arg CONFIG_IMAGE "${DOCKER_REPO}:config" \
    --arg NAME "$ENVIRONMENT_NAME-Cart" \
    --arg APP_IMAGE $DOCKER_REPO:cart \
    --arg APP_PORT 5001 \
    --arg CHECKOUT_SERVICE "checkout.${SERVICES_DOMAIN}:5002" \
    --arg CONFIG_VOLUME "${DOCKER_REPO}:config" \
    --arg SIGNALFX_ENDPOINT_URL "${SIGNALFX_ENDPOINT_URL}" \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_LOG_STREAM_PREFIX_APP "cart-app" \
    --arg TASK_ROLE_ARN $task_role_arn \
    --arg EXECUTION_ROLE_ARN $execution_role_arn \
    --argjson ENVOY_CONTAINER_JSON "${envoy_container_json}" \
    -f "${DIR}/cart-task.json")
task_def=$(aws --profile "${AWS_PROFILE}" --region "${AWS_DEFAULT_REGION}" \
    ecs register-task-definition \
    --cli-input-json "$task_def_json")
cart_task_def_arn=($(echo $task_def \
    | jq -r '.taskDefinition | .taskDefinitionArn'))

# Checkout Task Definition
envoy_container_json=$(jq -n \
    --arg ENVOY_SERVICE_NAME "checkout" \
    --arg ENVOY_IMAGE $ENVOY_IMAGE \
    --arg VIRTUAL_NODE "mesh/$MESH_NAME/virtualNode/checkout-vn" \
    --arg APPMESH_XDS_ENDPOINT "${APPMESH_XDS_ENDPOINT}" \
    --arg ENVOY_LOG_LEVEL $envoy_log_level \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg AWS_LOG_STREAM_PREFIX_ENVOY "checkout-envoy" \
    -f "${DIR}/envoy-container.json")
task_def_json=$(jq -n \
    --arg CONFIG_IMAGE "${DOCKER_REPO}:config" \
    --arg NAME "$ENVIRONMENT_NAME-Checkout" \
    --arg APP_IMAGE $DOCKER_REPO:checkout \
    --arg APP_PORT 5002 \
    --arg CATALOG_SERVICE "catalog.${SERVICES_DOMAIN}:5004" \
    --arg PAYMENT_SERVICE "payment.${SERVICES_DOMAIN}:5003" \
    --arg CONFIG_VOLUME "${DOCKER_REPO}:config" \
    --arg SIGNALFX_ENDPOINT_URL "${SIGNALFX_ENDPOINT_URL}" \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_LOG_STREAM_PREFIX_APP "checkout-app" \
    --arg TASK_ROLE_ARN $task_role_arn \
    --arg EXECUTION_ROLE_ARN $execution_role_arn \
    --argjson ENVOY_CONTAINER_JSON "${envoy_container_json}" \
    -f "${DIR}/checkout-task.json")
task_def=$(aws --profile "${AWS_PROFILE}" --region "${AWS_DEFAULT_REGION}" \
    ecs register-task-definition \
    --cli-input-json "$task_def_json")
checkout_task_def_arn=($(echo $task_def \
    | jq -r '.taskDefinition | .taskDefinitionArn'))

# Catalog Task Definition
envoy_container_json=$(jq -n \
    --arg ENVOY_SERVICE_NAME "catalog-v1" \
    --arg ENVOY_IMAGE $ENVOY_IMAGE \
    --arg VIRTUAL_NODE "mesh/$MESH_NAME/virtualNode/catalog-vn" \
    --arg APPMESH_XDS_ENDPOINT "${APPMESH_XDS_ENDPOINT}" \
    --arg ENVOY_LOG_LEVEL $envoy_log_level \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg AWS_LOG_STREAM_PREFIX_ENVOY "catalog-envoy" \
    -f "${DIR}/envoy-container.json")
task_def_json=$(jq -n \
    --arg CONFIG_IMAGE "${DOCKER_REPO}:config" \
    --arg NAME "$ENVIRONMENT_NAME-Catalog" \
    --arg APP_IMAGE $DOCKER_REPO:catalog \
    --arg APP_PORT 5004 \
    --arg CATALOG_SERVICE "catalog.${SERVICES_DOMAIN}:5004" \
    --arg REFRESHDB_SERVICE "refreshdb.${SERVICES_DOMAIN}:5008" \
    --arg CONFIG_VOLUME "${DOCKER_REPO}:config" \
    --arg SIGNALFX_ENDPOINT_URL "${SIGNALFX_ENDPOINT_URL}" \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_LOG_STREAM_PREFIX_APP "catalog-app" \
    --arg TASK_ROLE_ARN $task_role_arn \
    --arg EXECUTION_ROLE_ARN $execution_role_arn \
    --argjson ENVOY_CONTAINER_JSON "${envoy_container_json}" \
    -f "${DIR}/catalog-task.json")
task_def=$(aws --profile "${AWS_PROFILE}" --region "${AWS_DEFAULT_REGION}" \
    ecs register-task-definition \
    --cli-input-json "$task_def_json")
catalog_task_def_arn=($(echo $task_def \
    | jq -r '.taskDefinition | .taskDefinitionArn'))

# Catalog v2 Task Definition
envoy_container_json=$(jq -n \
    --arg ENVOY_SERVICE_NAME "catalog-v2" \
    --arg ENVOY_IMAGE $ENVOY_IMAGE \
    --arg VIRTUAL_NODE "mesh/$MESH_NAME/virtualNode/catalog-vn" \
    --arg APPMESH_XDS_ENDPOINT "${APPMESH_XDS_ENDPOINT}" \
    --arg ENVOY_LOG_LEVEL $envoy_log_level \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg AWS_LOG_STREAM_PREFIX_ENVOY "catalog-v2-envoy" \
    -f "${DIR}/envoy-container.json")
task_def_json=$(jq -n \
    --arg CONFIG_IMAGE "${DOCKER_REPO}:config" \
    --arg NAME "$ENVIRONMENT_NAME-Catalog-v2" \
    --arg APP_IMAGE $DOCKER_REPO:catalog \
    --arg APP_PORT 5004 \
    --arg CATALOG_SERVICE "catalog.${SERVICES_DOMAIN}:5004" \
    --arg REFRESHDB_SERVICE "refreshdb.${SERVICES_DOMAIN}:5008" \
    --arg CONFIG_VOLUME "${DOCKER_REPO}:config" \
    --arg SIGNALFX_ENDPOINT_URL "${SIGNALFX_ENDPOINT_URL}" \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_LOG_STREAM_PREFIX_APP "catalog-v2-app" \
    --arg TASK_ROLE_ARN $task_role_arn \
    --arg EXECUTION_ROLE_ARN $execution_role_arn \
    --argjson ENVOY_CONTAINER_JSON "${envoy_container_json}" \
    -f "${DIR}/catalog-task.json")
task_def=$(aws --profile "${AWS_PROFILE}" --region "${AWS_DEFAULT_REGION}" \
    ecs register-task-definition \
    --cli-input-json "$task_def_json")
catalog_v2_task_def_arn=($(echo $task_def \
    | jq -r '.taskDefinition | .taskDefinitionArn'))

# Payment Task Definition
envoy_container_json=$(jq -n \
    --arg ENVOY_SERVICE_NAME "payment" \
    --arg ENVOY_IMAGE $ENVOY_IMAGE \
    --arg VIRTUAL_NODE "mesh/$MESH_NAME/virtualNode/payment-vn" \
    --arg APPMESH_XDS_ENDPOINT "${APPMESH_XDS_ENDPOINT}" \
    --arg ENVOY_LOG_LEVEL $envoy_log_level \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg AWS_LOG_STREAM_PREFIX_ENVOY "payment-envoy" \
    -f "${DIR}/envoy-container.json")
task_def_json=$(jq -n \
    --arg CONFIG_IMAGE "${DOCKER_REPO}:config" \
    --arg NAME "$ENVIRONMENT_NAME-Payment" \
    --arg APP_IMAGE $DOCKER_REPO:payment \
    --arg APP_PORT 5003 \
    --arg CONFIG_VOLUME "${DOCKER_REPO}:config" \
    --arg SIGNALFX_ENDPOINT_URL "${SIGNALFX_ENDPOINT_URL}" \
    --arg AWS_REGION $AWS_DEFAULT_REGION \
    --arg ECS_SERVICE_LOG_GROUP $ecs_service_log_group \
    --arg AWS_LOG_STREAM_PREFIX_APP "payment-app" \
    --arg TASK_ROLE_ARN $task_role_arn \
    --arg EXECUTION_ROLE_ARN $execution_role_arn \
    --argjson ENVOY_CONTAINER_JSON "${envoy_container_json}" \
    -f "${DIR}/payment-task.json")
task_def=$(aws --profile "${AWS_PROFILE}" --region "${AWS_DEFAULT_REGION}" \
    ecs register-task-definition \
    --cli-input-json "$task_def_json")
payment_task_def_arn=($(echo $task_def \
    | jq -r '.taskDefinition | .taskDefinitionArn'))

