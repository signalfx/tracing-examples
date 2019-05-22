#!/usr/bin/env node
// Initialize the tracer and initiate auto-instrumentation
// of all supported libraries and frameworks.  In this client example,
// we instrumented the http module used by the wordExplorer client module.
const tracer = require('./wordExplorer/tracer');

// Note that importing other modules should occur after init()
// to ensure their supported dependencies have been auto-instrumented.
const yargs = require('yargs');
const client = require('./wordExplorer/client');


function printWord(response) {
  console.log('++++++++++++++++++++++++++++++++++');
  console.log('WordExplorer Response: \n');
  if (!response.message.word) {
    console.log(response.message);
  } else {
    const properties = ['word', 'pos', 'meaning', 'synonyms', 'usageNote'];
    properties.forEach((prop) => {
      console.log(`${prop}: ${response.message[prop]}`);
    });
  }
  console.log('\n++++++++++++++++++++++++++++++++++\n');
}

function printList(response) {
  console.log('++++++++++++++++++++++++++++++++++');
  console.log('WordExplorer Response: \n');

  const properties = ['word', 'pos', 'meaning', 'synonyms', 'usageNote'];
  for (const [key, value] of Object.entries(response)) {
    for (let i = 0; i < value.length; i++) {
      properties.forEach((prop) => {
        console.log(`${prop}: ${value[i][prop]}`);
      });
      console.log('\n----------------------------------\n');
    }
  }
  console.log('End of List');
  console.log('\n++++++++++++++++++++++++++++++++++\n');
}

yargs
    .usage('Usage: $0 <command> [options]')
    .scriptName('wordExplorer')
    .command('add <word> [usage]', 'Add word to vocabulary list.',
        {}, (argv) => {
          client.addWord(argv.word, argv.usage)
              .then(printWord);
        })

    .command('delete <word>', 'Delete word.', {}, (argv) => {
      client.deleteWord(argv.word)
          .then(printWord);
    })

    .command('explore <word>', 'Explore a word.', {}, (argv) => {
      client.exploreWord(argv.word)
          .then(printWord);
    })

    .command('list', 'Show vocabulary list.', {}, (argv) => {
      client.listWords()
          .then(printList);
    })

    .command('retrieve <word>', 'Retrieve word from your vocabulary list.',
        {}, (argv) => {
          client.viewWord(argv.word)
              .then(printWord);
        })

    .command('update <word> <usage>', 'Update word.', {}, (argv) => {
      client.updateWord(argv.word, argv.usage)
          .then(printWord);
    })

    .help().argv;
