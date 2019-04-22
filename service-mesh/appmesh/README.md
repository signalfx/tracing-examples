# E-Commerce on AWS App Mesh

App Mesh is a new service mesh offering from AWS. They provide a managed control
plane so that users are only required to deploy a simple data plane with Envoy.
A brief introduction to the core ideas can be read in their [documentation](https://docs.aws.amazon.com/app-mesh/latest/userguide/what-is-app-mesh.html).

The deployment scaffolding of this example has been adopted, in large part, from
the [AWS App Mesh Examples](https://github.com/aws/aws-app-mesh-examples).

Build the application images used for this example by following the instructions at [../app-src](../app-src).

## Setup

Set the following environment variables to configure the infrastructure deployment:

```
AWS_PROFILE
AWS_REGION
AWS_DEFAULT_REGION
ENVIRONMENT_NAME
MESH_NAME
KEY_PAIR_NAME
ENVOY_IMAGE
CLUSTER_SIZE
SERVICES_DOMAIN
DOCKER_REPO
```

Deploy the example VPC and ECS cluster, and App Mesh:

```bash
infra/vpc.sh
infra/ecs-cluster.sh
infra/appmesh-mesh.sh
```

Set an environment variable, `SIGNALFX_ENDPOINT_URL` for the trace endpoint. If sending directly to a Gateway, it would look similar to this:

```bash
export SIGNALFX_ENDPOINT_URL=http://gateway.servicesdomain:8080
```

If sending to an Agent running on the task host instead, substitute the hostname
with the literal, `POD_HOST_IP`.

```bash
export SIGNALFX_ENDPOINT_URL=http://POD_HOST_IP:9080
```

This will make Envoy and the apps send traces to the host at port `9080`.

Deploy the App Mesh virtual nodes, routers, routes, and services with the provided
Cloudformation template. This template has an included virtual node and virtual
service to handle egress to the SignalFx Gateway, if sending directly to that.
Set two environment variables, `GATEWAY_URL` and `GATEWAY_PORT`, `GatewayVirtualService`
and `GatewayVirtualNode` to match the actual Gateway deployment.

```bash
export GATEWAY_URL=gateway.${SERVICES_DOMAIN}
export GATEWAY_PORT=8080
./ecommerce-mesh-resources.sh
```

Build the config image and push it to the docker repository. The startup script
will read the value of `SIGNALFX_ENDPOINT_URL` and populate the Envoy
configuration accordingly.

```bash
cd config-image
./build.sh
```

## Running the example

Deploy and start the app using the adapted deployment script:

```bash
./ecs/ecommerce-app.sh
```

By default, all routing is done through `catalog-v1`. To modify this and enable
`catalog-v2`, edit the `CatalogRoute` in `ecommerce-mesh-resources.yaml` and
uncommment the second weighted target. Various weights can be assigned to each
node.

Apply the modified route with `ecommerce-mesh-resources.sh`.
