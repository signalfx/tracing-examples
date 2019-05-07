// The core of the auto-instrumentation process is made in the
// tracer.js module.  It consists of a single import and invocation
// of the 'signalfx-tracing' module.
const tracer = require('./deedScheduler/tracer');


// Note Koa module import must come after the init() that occurs
// in deedScheduler/tracer module import, as well as any modules that load it.
const Koa = require('koa');
const logger = require('koa-logger');
const scheduler = require('./deedScheduler');
const router = require('koa-router')();


const server = new Koa();


router
    .use('/deedScheduler', scheduler.router.routes());

server
    .use(logger())
    .use(router.routes(), router.allowedMethods())
    .listen(scheduler.config.serverPort, () => {
      console.log(`\n++++++++++++++++++++++++++++++++++++++++++++++++++++++

      Welcome to DeedScheduler.
      The server is listening on: ${scheduler.config.serverUrl}.
      Database version: ${scheduler.config.mysqlLib}
\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++\n`);
    });
