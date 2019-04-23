#!/bin/bash

set -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

# Creating Task Definitions
source ${DIR}/create-task-defs.sh

aws --profile "${AWS_PROFILE}" --region "${AWS_DEFAULT_REGION}" \
    cloudformation deploy \
    --stack-name "${ENVIRONMENT_NAME}-app" \
    --capabilities CAPABILITY_IAM \
    --template-file "${DIR}/ecommerce-app-task.yaml"  \
    --parameter-overrides \
    EnvironmentName="${ENVIRONMENT_NAME}" \
    ECSServicesDomain="${SERVICES_DOMAIN}" \
    AppMeshMeshName="${MESH_NAME}" \
    ApiTaskDefinition="${api_task_def_arn}" \
    CartTaskDefinition="${cart_task_def_arn}" \
    CheckoutTaskDefinition="${checkout_task_def_arn}" \
    CatalogTaskDefinition="${catalog_task_def_arn}" \
    CatalogV2TaskDefinition="${catalog_v2_task_def_arn}" \
    PaymentTaskDefinition="${payment_task_def_arn}" \
    RefreshDBTaskDefinition="${refreshdb_task_def_arn}"
