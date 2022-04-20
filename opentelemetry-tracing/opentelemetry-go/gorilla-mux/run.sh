#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

function cleanup {
  docker-compose -f "${DIR}/docker-compose.yaml" down
  kill 0 # kill background processes
}
trap cleanup EXIT

set -euxo pipefail

export OTEL_RESOURCE_ATTRIBUTES="deployment.environment=$(whoami)"
docker-compose -f "${DIR}/docker-compose.yaml" build
docker-compose -f "${DIR}/docker-compose.yaml" up
