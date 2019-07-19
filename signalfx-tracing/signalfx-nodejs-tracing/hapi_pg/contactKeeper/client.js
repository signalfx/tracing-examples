// http module auto-instrumentation will occur once
// the tracer is initialized, which occurs in
// the sourcing client script.

const http = require('http');
const {serverUrl} = require('./config');

const keeperUrl = `${serverUrl}/contactKeeper`;

function resolveData(res, resolve) {
  // resolves a Promise with parsed http response
  let data = '';
  res.on('data', (chunk) => {
    data += chunk;
  });
  res.on('end', () => {
    resolve(JSON.parse(data));
  });
}

function addContact(firstName, lastName, email) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${keeperUrl}/addContact`,
        {method: 'POST'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    const content = {firstName, lastName, email};
    req.setHeader('Content-Type', 'application/json');
    req.write(JSON.stringify(content));
    req.end();
  });
}

function deleteByID(id) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${keeperUrl}/contacts/${id}`,
        {method: 'DELETE'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    req.end();
  });
}

function deleteContact(fName, lName) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${keeperUrl}/contacts/?fName=${fName}&lName=${lName}`,
        {method: 'DELETE'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    req.end();
  });
}

function getContact(fName, lastName) {
  const lName = lastName ? lastName : '__ALL__';
  return new Promise((resolve, reject) => {
    const req = http.request(`${keeperUrl}/contacts/${fName}?lName=${lName}`,
        {method: 'GET'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    req.end();
  });
}

function listContacts() {
  return new Promise((resolve, reject) => {
    const req = http.request(`${keeperUrl}/contacts`,
        {method: 'GET'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    req.end();
  });
}

function updateByID(id, email) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${keeperUrl}/contacts/${id}`,
        {method: 'PUT'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    const content = {email};
    req.setHeader('Content-Type', 'application/json');
    req.write(JSON.stringify(content));
    req.end();
  });
}

function updateEmail(fName, lName, email) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${keeperUrl}/contacts/?fName=${fName}&lName=${lName}`,
        {method: 'PUT'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    const content = {email};
    req.setHeader('Content-Type', 'application/json');
    req.write(JSON.stringify(content));
    req.end();
  });
}

module.exports = {addContact, getContact, deleteByID,
  deleteContact, listContacts, updateByID, updateEmail};
