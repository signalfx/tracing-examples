// The core of the auto-instrumentation process is made in the
// tracer.js module.  It consists of a single import and invocation
// of the 'signalfx-tracing' module.
const tracer = require('./wordExplorer/tracer');

// Note koa module import must come after init() that occurs
// in word_explorer/tracer module import, as well as any
// modules that load it.
const Koa = require('koa');
const wordExplorer = require('./wordExplorer');
const logger = require('koa-logger');
const router = require('koa-router')();

const server = new Koa();

router
    .use('/wordExplorer', wordExplorer.router.routes());

server
    .use(logger())
    .use(router.routes(), router.allowedMethods())
    .listen(wordExplorer.config.serverPort, function() {
      console.log(`\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    Welcome to WordExplorer.
    The server is listening on ${wordExplorer.config.serverUrl}.
\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n`);
    });
