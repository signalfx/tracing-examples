#!/usr/bin/env node

const tracer = require('./contactKeeper/tracer');

const yargs = require('yargs');
const client = require('./contactKeeper/client');


function printRes(response) {
  console.log('++++++++++++++++++++++++++++++++++');
  console.log('ContactKeeper: \n');
  if (response.message) {
    console.log(response.message);
  } else if (response.err) {
    console.error('ERROR:', response.err);
  }
  console.log('\n++++++++++++++++++++++++++++++++++\n');
}

function printList(response) {
  if (response.message) {
    return printRes(response);
  }
  console.log('+++++++++++++++++++++++++++++++++++');
  console.log('             Contact(s)');
  console.log('----++---------++++---------++-----\n');
  const properties = ['id', 'firstname', 'lastname', 'email'];

  for (const [key, value] of Object.entries(response)) {
    properties.forEach((prop) => {
      console.log(`${prop}: ${value[prop]}`);
    });
    console.log('\n-----++---------++++---------++----');
  }
  console.log('\n+++++++++++++++++++++++++++++++++++\n');
}

yargs
    .usage('Usage: $0 <command> [options]')
    .scriptName('contactKeeper')
    .command('add <firstName> <lastName> <email>',
        'Add a new contact to your address book.',
        {}, (argv) => {
          client.addContact(argv.firstName, argv.lastName, argv.email)
              .then(printRes);
        })

    .command('delete <fName> <lName>', 'Delete a contact.', {}, (argv) => {
      client.deleteContact(argv.fName, argv.lName)
          .then(printRes);
    })

    .command('deleteByID <id>', 'Delete a contact by ID.', {}, (argv) => {
      client.deleteByID(argv.id)
          .then(printRes);
    })

    .command('get <fName> [lName]', 'Get contact from your ContactKeeper.',
        {}, (argv) => {
          client.getContact(argv.fName, argv.lName)
              .then(printList);
        })

    .command('list', 'Show list.', {}, (argv) => {
      client.listContacts()
          .then(printList);
    })

    .command('update <fName> <lName> <email>', 'Update a contact\'s email.',
        {}, (argv) => {
          client.updateEmail(argv.fName, argv.lName, argv.email)
              .then(printRes);
        })

    .command('updateByID <id> <email>', 'Update a contact\'s email by ID.',
        {}, (argv) => {
          client.updateByID(argv.id, argv.email)
              .then(printRes);
        })

    .help().argv;
