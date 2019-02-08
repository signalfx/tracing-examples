# PHP Tracing Example

This example shows a mix of manual and auto-instrumented PHP code.  It uses the
[Symfony demo app](https://github.com/symfony/demo) to provide an HTTP server
that the [example.php](./example.php) script makes a request to.

## Running

To run this example, first install the PHP tracer by using your [preferred
installation method](https://docs.signalfx.com/en/latest/apm/apm-instrument/apm-php.html).
Note that this must be done on in a Linux environment, as MacOS is not
supported.  If you do not wish to install the tracer yourself, you can use the
Dockerfile provided in this directory.

**If you do not have the [Smart Agent deployed on the same
host](https://docs.signalfx.com/en/latest/apm/apm-deployment/smart-agent.html)**,
you can configure the PHP tracer to send directly to our global ingest
server by setting the following envvars in your shell before running the next
command:

```sh
$ export SIGNALFX_ACCESS_TOKEN=<your org token>
$ export SIGNALFX_ENDPOINT_URL=https://ingest.signalfx.com/v1/trace  # Or the appropriate ingest for your realm
```

Next, start up the Symfony demo app:

```sh
$ cd symfony_demo
$ composer install
$ SIGNALFX_SERVICE_NAME=symfony_demo SIGNALFX_AUTOFINISH_SPANS=true php -S 127.0.0.1:8080 public/index.php &
```

This will start up the demo server on port 8080, which the example script is
hardcoded to use.  The `SIGNALFX_AUTOFINISH_SPANS=true` envvar is set because
of a current limitation with the Symfony demo app leaving a span open
improperly.

Now run the example PHP app on the same host:

```sh
$ SIGNALFX_SERVICE_NAME=demo-app php -S 0.0.0.0:8081 example.php &
$ curl localhost:8081
```

This will serve the [example.php script](./example.php) and do a request to it.
You can see the traces by going to your org in SignalFx and looking for the
`demo-app` service.  The value that is set for `SIGNALFX_SERVICE_NAME` is the
service name that will be used for all traces eminating from the app.
Generally, this name should be fairly generic and should identify the type of
app, not the specific instance or deployed identity of the app.
