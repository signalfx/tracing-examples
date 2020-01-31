# Spring Boot Auto-Instrumentation Example

This is an example of automatically producing traces using the
[SignalFx Java Agent](https://github.com/signalfx/signalfx-java-tracing).
This example is of a simple wishlist web app that is auto-instrumented by providing
the agent jar via the required JVM command-line option.

## Building the example app

To run this example locally and send traces to your available Smart Agent or Gateway,
please clone this repository and from this directory do the following:

```bash
$ # docker-compose is required for postgres instance
$ pip install docker-compose
$ # download the newest version of the agent
$ sudo curl -sSL -o /opt/signalfx-tracing.jar 'https://search.maven.org/remote_content?g=com.signalfx.public&a=signalfx-java-agent&v=LATEST&c=unbundled'
$ ./run
```

The Java Agent and this application configuration assume that your Smart Agent
or Gateway is accepting traces at http://localhost:9080/v1/trace.  If this is not the case,
you can set the `SIGNALFX_ENDPOINT_URL` environment variable to the desired url to suit your
environment before launching the server and client.

## Accessing the Wishlist

The example application is a simple wishlist that allows adding desired items for
individual users.  By default, you can access the site via http://localhost:8080/ in your
web browser.
