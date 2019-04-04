#!/usr/bin/env node
// Initialize the tracer and initiate auto-instrumentation of
// all supported libraries and frameworks.  In this client example,
// we instrumented the http module used by the
// DeedScheduler client module.
const tracer = require('./deedScheduler/tracer');

// Note that importing other modules should occur after
// init() to ensure their supported
// dependencies have been auto-instrumented.
const yargs = require('yargs');
const client = require('./deedScheduler/client');


function format(response) {
  console.log('++++++++++++++++++++++++++++++++++');
  console.log('DeedScheduler Response: \n');
  console.log(response);
  console.log('++++++++++++++++++++++++++++++++++\n');
}

function printResponse(response) {
   response.message ? format(response.message) : format(response);
}

yargs
    .usage('Usage: $0 <command> [options]')
    .scriptName('deedScheduler')

    .command('add <deed> <note> <day>', 'Add a task to deedScheduler.',
        {}, (argv) => {
          client.addItem(argv.deed, argv.note, argv.day)
              .then(printResponse)
              .catch(console.error);
        })

    .command('delete [deed] [day]', 'Delete deed.', {}, (argv) => {
      client.deleteDeed(argv.deed, argv.day)
          .then(printResponse)
          .catch(console.error);
    })

    .command('list [day]', 'Show deeds list.', {}, (argv) => {
      client.listDeeds(argv.day)
          .then(printResponse)
          .catch(console.error);
    })

    .command('view <deed> [day] [status]', 'Retrieve task from scheduler.',
        {}, (argv) => {
          client.viewDeed(argv.deed, argv.day, argv.status)
              .then(printResponse)
              .catch(console.error);
        })

    .command('update <deed> <day> <status>', 'Update status of deed (uncompleted - 0, completed - 1).',
        {}, (argv) => {
          client.updateDeed(argv.deed, argv.day, argv.status)
              .then(printResponse)
              .catch(console.error);
        })

    .help().argv;
