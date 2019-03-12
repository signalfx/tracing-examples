#!/usr/bin/env node
// Initialize the tracer and initiate auto-instrumentation of all supported libraries
// and frameworks.  In this client example, we instrumented the http module used by the
// snowman client module.
const tracer = require('./snowman/tracer')

// Note that importing other modules should occur after init() to ensure their supported
// dependencies have been auto-instrumented.
const yargs = require('yargs')
const fs = require('fs')
const client = require('./snowman/client')

function writeId(id) {
  // Write a new game's id to local .snowman file
  return new Promise((resolve, reject) => {
    fs.writeFile('.snowman', id, (err) => {
      if (err) {
        reject(err)    
      } else {
        resolve()
      }
    })
  })
}

function getId() {
  // Obtain an existing game's id from local .snowman
  return new Promise((resolve, reject) => {
    fs.readFile('.snowman', (err, data) => {
      if (err) {
        reject(`Error obtaining game id: ${err}.  Be sure to run "new" to create a game.`)
      } else {
        const id = data.toString() 
        resolve(id)
      }
    })
  })
}

function printResponse(response) {
  const properties = ['message', 'guesses', 'progress', 'remainingMisses']
  for (let i = 0; i < properties.length; i++) {
    const prop = properties[i]
    if (response[prop]) {
      console.log(`${prop}: ${response[prop]}`)
    }
 }
}

yargs
  .scriptName('snowman')
  .command('new', 'Make a new game.', {}, (argv) => {
    client.newGame().then((response) => {
      return writeId(response.id).then(() => {
        printResponse(response)
      }).catch((e) => console.error(e))
    }).catch((e) => console.error(e))
  })
  .command('guess [letter]', 'Make a guess.', {}, (argv) => {
    getId().then((id) => { 
      client.makeGuess(argv.letter, id).then((response) => {
        printResponse(response)
        if (response.remainingMisses !== undefined) {
          // render the snowman
          for (let i = response.remainingMisses; i < 8; i++ ) {
            console.log(client.snowman[i])
          }
        }
      }).catch((e) => console.error(e))
    }).catch((e) => console.error(e))
  })
  .command('answer', 'Report the answer', {}, (argv) => {
    getId().then((id) => { 
      client.getAnswer(id).then(response => {
        printResponse(response)
      }).catch((e) => console.error(e))
    }).catch((e) => console.error(e))
  })
  .command('delete', 'Delete your current game', {}, (argv) => {
    getId().then((id) => { 
      client.deleteGame(id).then(response => {
        printResponse(response)
      }).catch((e) => console.error(e))
    }).catch((e) => console.error(e))
  })
  .help().argv