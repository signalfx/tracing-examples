# Express.js Auto-Instrumentation Example

This is an example of automatically producing distributed traces using the
[SignalFx Tracing Library for JavaScript](https://github.com/signalfx/signalfx-nodejs-tracing).
Please examine the instrumented [client](./client.js) and [server](./server.js) for
some basic patterns in accessing the instrumentations of an [http](https://nodejs.org/api/http.html)
client and an [Express](http://expressjs.com/) application. This example is of a simple
guess-the-word game named "Snowman" that is auto-instrumented by a lone
[tracer invocation](./snowman/tracer.js).

## Building the example app and client

To run this example locally and send traces to your available Smart Agent or Gateway,
from this directory do the following:

```bash
  $ npm install
  $ # Run the server from one shell session:
  $ npm start 
  $ # Run the client commands from another:
  $ # You can also use ` ./client.js --help` directly
  $ npm run client -- --help
  Snowman [command]

  Commands:
    snowman new             Make a new game.
    snowman guess [letter]  Make a guess.
    snowman answer          Report the answer
    snowman delete          Delete your current game
```

The `signalfx-tracing` module and this application configuration assume that your Smart Agent
or Gateway is accepting traces at http://localhost:9080/v1/trace.  If this is not the case,
you can set the `SIGNALFX_INGEST_URL` environment variable to the desired url to suit your
environment before launching the server and client.

## Playing Snowman

The example application is a simple word game where you must guess an unknown word by its
constituent letters from a pool of 1000 randomly selected Unix `words` file entries.  Each
client request will automatically create an initiating parent span for distributed propagation
to some basic REST api endpoints implemented via Express.

```bash
  $ # You can also use ` ./client.js new` directly
  $ npm run client new
  message: Game successfully created.
  progress: _________

  $ npm run client guess x
  message: Error: There is no "x" in the word.
  guesses: x
  progress: _________
  remainingMisses: 7
  ( ............... )

  $ npm run client answer
  message: The answer was: manducate.  Deleting game.
```
