'use strict';

const db = require('./pgDriver');

// Helper function
function countRows(fName, lName) {
  return db.countRows(fName, lName);
}

function addContact(firstName, lastName, email) {
  return db.addContact(firstName, lastName, email);
}

async function deleteContact(fName, lName) {
  const rows = await countRows(fName, lName);
  if (rows > 1) {
    return ('__DUPLICATE__');
  }
  return db.deleteContact(fName, lName);
}

async function deleteByID(id) {
  return db.deleteByID(id);
}

async function getContact(fName, lName) {
  return db.getContact(fName, lName);
}

function listContacts() {
  return db.listContacts();
}

async function updateEmail(fName, lName, email) {
  const rows = await countRows(fName, lName);
  if (rows > 1) {
    return ('__DUPLICATE__');
  }
  return db.updateEmail(fName, lName, email);
}

async function updateByID(id, email) {
  return db.updateByID(id, email);
}

module.exports = {addContact, deleteContact, deleteByID,
  listContacts, getContact, updateByID, updateEmail};
