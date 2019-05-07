# MySQL Auto-Instrumentation Example

This is an example of automatically producing distributed traces using the
[SignalFx Tracing Library for JavaScript](https://github.com/signalfx/signalfx-nodejs-tracing).
Please examine the instrumented [client](./client.js) and [server](./server.js) for
some basic patterns in accessing the instrumentations of a [http](https://nodejs.org/api/http.html)
client and a [MySQL](https://www.mysql.com) database.
You have the options of either running this example
with the [MySQL](https://github.com/mysqljs/mysql) module or
the [MySQL2](https://github.com/sidorares/node-mysql2) module.

In ths example, we have a simple "To Do" application named
"DeedScheduler" that is auto-instrumented by a lone
[tracer invocation](./deedScheduler/tracer.js).

## Building the example app and client

To run this example locally and send traces to your available Smart Agent or Gateway,
 from this directory do the following:

In one terminal:
```bash
  $ # start up the MySQL server
  $ npm run mysql
  $ # install app requirements
  $ npm install
  $ # You may specify the version of MySQL you want to run
  $ # through environment variables as follows (Default: MySQL2):
  $ # Ex: export DEEDSCHEDULER_MYSQL_CLIENT=1 (for MySQL)
  $ #     export DEEDSCHEDULER_MYSQL_CLIENT=2 (for MySQL2)
  $ # Run the server from one shell session:
  $ npm start

  ++++++++++++++++++++++++++++++++++++++++++++++++++++++

        Welcome to DeedScheduler.
        The server is listening on http://localhost:3001.
        Database version: MySQL2

  +++++++++++++++++++++++++++++++++++++++++++++++++++++++

```
*Note:* Allow a few seconds between `npm run mysql` and `npm start`, to ensure the MySQL server is ready for use.

From a different terminal, you may run the client commands.
```bash
  $ # You can also use ` ./client.js help` directly
  $ npm run client help
  Usage: deedScheduler <command> [options]

  Commands:
    deedScheduler add [deed] [note] [day]               Add a task to deedScheduler.
    deedScheduler delete [deed] [day]                   Delete deed.
    deedScheduler list [day]                            Show deeds list.
    deedScheduler view [deed] [day] [status]            Retrieve task from scheduler.
    deedScheduler update [deed] [day] [status]          Update status of deed (completed- 1, uncompleted - 0).


```
Note: `status` above is an integer. 0 for incomplete, 1 for complete. (The default is 0)

The `signalfx-tracing` module and this application configuration assume that your Smart Agent
or Gateway is accepting traces at http://localhost:9080/v1/trace.  If this is not the case,
you can set the `SIGNALFX_ENDPOINT_URL` environment variable to the desired url to suit your
environment before launching the server and client.

## Using

The DeedScheduler allows you to add, retrieve, update and delete entries as part of a your to do list. Each
client request will automatically create an initiating parent span for distributed propagation
to some basic REST api endpoints implemented via [Koa](https://koajs.com).


Example usage:
```bash
  $ # You can also use ` ./client.js add 'coding' 'Create a TODO app' Sunday` directly
  $ npm run client add 'coding' 'Create a TODO app' Sunday

  ++++++++++++++++++++++++++++++++++
  DeedScheduler Response:

  You just added coding on Sunday to your deedScheduler!
  Number of rows affected: Rows affected: 1
  ++++++++++++++++++++++++++++++++++


  $ npm run client list Sunday

  ++++++++++++++++++++++++++++++++++
  DeedScheduler Response:

  [ { id: 3,
      deed: 'coding',
      note: 'Create a TODO app',
      day: 'Sunday',
      date: '2019-04-22T20:29:25.000Z',
      completed: 0 } ]
  ++++++++++++++++++++++++++++++++++


```
