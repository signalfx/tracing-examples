# E-Commerce with Istio

This demo assumes that the necessary CRDs are already installed Istio, has been
set up on a suitable cluster and that `kubectl` has been set up to point to that
cluster.

For an easy set of policies to start with, use the `istio-demo.yaml` provided
with the Istio release.

Build the images used for this example by following the instructions at [../app-src](../app-src)

## Setup

In the mesh configmap, enable tracing and set the the zipkin address to point to the ingest
endpoint desired.

```
apiVersion: v1
kind: ConfigMap
metadata:
  name: istio
  namespace: istio-system
  ...
data:
  mesh: |-
    enableTracing: true

    tracing:
      zipkin:
        address: <ingest address>
```

Create a separate namespace for the example application and label it for automatic sidecar injection:

```
$ kubectl create namespace ecommerce-example
$ kubectl label namespace ecommerce-example istio-injection=enabled
```

Create a secret with your SignalFx Access Token:

    $ kubectl create secret generic --namespace ecommerce-example --from-literal access-token=<ACCESS_TOKEN> signalfx-access-token

If your deployment will be pointing to a Gateway, set a config value for the Gateway's address:

```
# assuming that the Gateway is exposed with a service in the default namespace
$ kubectl create configmap --namespace ecommerce-example signalfx-ingest --from-literal signalfx-ingest-url=http://gateway.default.svc.cluster.local:8080
```

If sending traces through an Agent on the host, put `POD_HOST_IP` as the host
instead, when creating the config. The app will replace this with the host IP from
the K8s downward API.

    $ kubectl create configmap --namespace ecommerce-example signalfx-ingest --from-literal signalfx-ingest-url=http://POD_HOST_IP:9080

Apply a bootstrap configmap to override some of the default tracing options in
Istio's Envoy config:

    $ kubectl apply -f envoy-signalfx.yaml

Apply the destination rules for the apps:

    $ kubectl apply -f destination-rule-all.yaml

A modified sidecar injection config is provided to set the Envoy service cluster name
to the app name. This reduces clutter on the service map by only displaying the
Envoy services we want to stand out.

    $ kubectl apply -f istio-demo-injector-config.yaml

Optionally, install the SignalFx Istio Mixer adapter from [here](https://github.com/signalfx/signalfx-istio-adapter).

## Run the example

Deploy the ECommerce application, substituting the docker repo where images were deployed:

    $ sed 's/DOCKER_REPO/'"${DOCKER_REPO}"'/g' ecommerce-app.yaml | kubectl apply -f -

Different routing rules can be experimented with by applying any of the Virtual
Service definitions available in this folder to control traffic between v1 and
v2 of the Catalog app.

    $ kubectl apply -f catalog-<v1_percentage>-<v2_percentage>.yaml

For the purposes of this example, the Envoy sidecars of the catalog app have
been given service names `catalog-v1` and `catalog-v2` to differentiate them
from the underlying service. The Envoy traces will show the routing happening
between the two hosts in the catalog virtual service.
