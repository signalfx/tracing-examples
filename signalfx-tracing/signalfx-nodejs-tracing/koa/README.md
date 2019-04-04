# Koa 2 Auto-Instrumentation Example

This is an example of automatically producing distributed traces using the
[SignalFx Tracing Library for JavaScript](https://github.com/signalfx/signalfx-nodejs-tracing).
Please examine the instrumented [client](./client.js) and [server](./server.js) for
some basic patterns in accessing the instrumentations of a [http](https://nodejs.org/api/http.html)
client and a [Koa](https://www.koajs.com) application.

In this example, we have a simple vocabulary exploration application, named "WordExplorer",
that is auto-instrumented by a lone
[tracer invocation](./deedScheduler/tracer.js). Its dictionary functionality is based on making calls to [Wordnet](https://wordnet.princeton.edu)
through a [WordNet API](https://github.com/morungos/wordnet) and results are stored in [MongoDB](http://www.mongodb.org/).

## Building the example app and client

To run this example locally and send traces to your available Smart Agent or Gateway,
do the following from this directory:

In one terminal:
```bash
  $ # start up the MongoDB server
  $ npm run mongo
  $ npm install
  $ # Run the server from one shell session:
  $ npm start
  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

       Welcome to WordExplorer.
       The server is listening on http://localhost:3000.

  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

  Database connection established

```

Note: Allow a few seconds between `npm run mongo` and `npm start`, to ensure that the MongoDB server is ready to go.

From a different terminal, you may run the client commands.
```bash
  $ # You may also use ` ./client.js help` directly
  $ npm run client help
  Usage: wordExplorer <command> [options]

  Commands:
    wordExplorer add [word] [usage]         Add word to vocabulary list.
    wordExplorer delete [word]              Delete word.
    wordExplorer explore [word]             Explore a word.
    wordExplorer list                       Show vocabulary list.
    wordExplorer retrieve [word]            Retrieve word from your vocabulary list.
    wordExplorer update [word] [usage]      Update word.




```

The `signalfx-tracing` module and this application configuration assume that your Smart Agent
or Gateway is accepting traces at http://localhost:9080/v1/trace.  If this is not the case,
you can set the `SIGNALFX_ENDPOINT_URL` environment variable to the desired url to suit your
environment before launching the server and client.

## Using

The WordExplorer allows you to add, retrieve, update and delete entries as part of a your vocabulary list. Each
client request will automatically create an initiating parent span for distributed propagation
to some basic REST api endpoints implemented via [Koa](https://koajs.com).


```bash
  $ # You may also use ` ./client.js explore create` directly
  $ npm run client explore create

++++++++++++++++++++++++++++++++++
WordExplorer Response:

word: create
pos: v
meaning: create or manufacture a man-made product
usageNote:  We produce more cars than we can sell

++++++++++++++++++++++++++++++++++


```
