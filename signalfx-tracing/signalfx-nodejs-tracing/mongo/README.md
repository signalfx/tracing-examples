# MongoDB Auto-Instrumentation Example

This is an example of automatically producing distributed traces using the
[SignalFx Tracing Library for JavaScript](https://github.com/signalfx/signalfx-nodejs-tracing).
Please examine the instrumented [client](./client.js) and [server](./server.js) for
some basic patterns in accessing the instrumentations of a [Mongoose](http://mongoosejs.com/)
client (via [mongodb-core](https://github.com/mongodb-js/mongodb-core)) and a basic
[http](https://nodejs.org/api/http.html) server. This example is of a simple
logging system that is auto-instrumented by a lone [tracer invocation](./logger/tracer.js).

## Building the example app and client

To run this example locally and send traces to your available Smart Agent or Gateway,
please clone this repository and from this directory do the following:

```bash
  $ npm install
  $ # Start the MongoDB Docker container
  $ npm run startMongo
  $ # Run the server from one shell session:
  $ npm start
  $ # Establish a logging client session in another
  $ npm run client
  log ~ Note to self:
  log ~ Trace these requests
  log ~ /q
  Log saved: ffff3cd0-445f-11e9-895b-7fdb2eba2840
```

The `signalfx-tracing` module and this application configuration assume that your Smart Agent
or Gateway is accepting traces at http://localhost:9080/v1/trace.  If this is not the case,
you can set the `SIGNALFX_ENDPOINT_URL` environment variable to the desired url to suit your
environment before launching the server and client.

## Using the logging client

The example application is a simple note-taking client where you initialize a logging
session to jot down some text and quit by entering `/q`.  A save confirmation will appear
and your logged remarks are retrievable by its log id.

```bash
  $ npm run client get ffff3cd0-445f-11e9-895b-7fdb2eba2840
  Jan 1 1970 00:00:01: Note to self:
  Jan 1 1970 00:00:01: Trace these requests
```
