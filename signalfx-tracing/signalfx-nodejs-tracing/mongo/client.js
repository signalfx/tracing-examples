#!/usr/bin/env node
// Initialize the tracer and initiate auto-instrumentation of all supported libraries
// and frameworks.  In this client example, we instrumented the mongoose module used by the
// logger's client module.
const tracer = require('./logger/tracer')

// Note that importing other modules should occur after init() to ensure their supported
// dependencies have been auto-instrumented.
const yargs = require('yargs')
const client = require('./logger/client')

yargs
  .scriptName('mongo-logger')
  .command('get [logId]', 'Retrieve a log by its id', {}, (argv) => {
    if (argv.logId.length !== 36 && argv.logId !== 'trigger_error') {
      console.log(`Invalid logId: ${argv.logId}.`)
      return
    }
    client.getLog(argv.logId).then((response) => {
      console.log('Retrieved:')
      console.log(response)
    }).catch((err) => {
      console.log('Error Occurred :')
      console.error(err)
    })
  })
  .command('$0', 'Logger session', {}, (argv) => {
    client.logPrompt()
  })
  .help().argv
