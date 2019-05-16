'use strict';

const tracer = require('./contactKeeper/tracer');

const Hapi = require('@hapi/hapi');
const config = require('./contactKeeper/config');
const server = Hapi.server({
  host: config.serverHost,
  port: config.serverPort,
});

server.route(require('./contactKeeper/router'));

const init = async () => {
  await server.start();
  console.log(`\n++++++++++++++++++++++++++++++++++++++++++++++++++++++

   Welcome to ContactKeeper.
   The server is listening on: ${server.info.uri}.

+++++++++++++++++++++++++++++++++++++++++++++++++++++++\n`);

  process.on('unhandledRejection', (err) => {
    console.log(err);
    process.exit(1);
  });
};

init();
