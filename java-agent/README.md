# Java Agent

This example shows the use of the [SignalFx JVM agent for
tracing](https://github.com/signalfx/signalfx-trace-java).  The Java agent
performs bytecode manipulation of the target application using the [JVM
Instrumentation](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html)
interface and enables tracing of many popular libraries and frameworks without
modification of the target application's source code.

Here are some environment variables you might need to set to configure the Java
agent:
```
$ # This is the default endpoint url
$ export SIGNALFX_ENDPOINT_URL=http://localhost:9080/v1/trace

$ # Replace this with your application's common name
$ export SIGNALFX_SERVICE_NAME=unnamed-java-app
```

To obtain the latest Java agent, download it to your host's filesystem:

```
curl -sSL -o /opt/signalfx-tracing.jar 'https://search.maven.org/remote_content?g=com.signalfx.public&a=signalfx-java-agent&v=LATEST&c=unbundled'
```

Make sure it is not writable by unsecured users since modifications to the
agent could easily compromise your application.  Then simply add the flag
`-javaagent:/opt/signalfx-tracing.jar` to your Java invocation and run your
application otherwise as normal.

You can run this particular example by first packaging the dependencies with
Maven and the running it with `-javaagent:`:

```sh
$ # Export the environment variables in this shell as shown above for your org
$ mvn package

$ # Run a Redis server locally that the example app will use
$ docker run -d --name redis-tracing-test -p 6379:6379 redis

$ java -javaagent:/opt/signalfx-tracing.jar -jar target/java-agent-example-1.0-SNAPSHOT-shaded.jar https://google.com
```
