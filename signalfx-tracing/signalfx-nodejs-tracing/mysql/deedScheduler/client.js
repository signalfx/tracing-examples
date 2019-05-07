// Here we import the tracer before the request library
// to ensure that its http dependency is instrumented
const tracer = require('./tracer');


const http = require('http');
const {serverUrl} = require('./config');
const deedSchedulerUrl = `${serverUrl}/deedScheduler`;


function resolveData(res, resolve) {
  // resolves a Promise with parsed http response
  let data = '';
  res.on('data', (d) => {
    data += d;
  });
  res.on('end', () => {
    resolve(JSON.parse(data));
  });
}


function addItem(deed, note, day) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${deedSchedulerUrl}/add`,
        {method: 'POST'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    const content = {deed, note, day};
    req.setHeader('Content-Type', 'application/json');
    req.write(JSON.stringify(content));
    req.end();
  });
}


function deleteDeed(deed, day) {
  return new Promise((resolve, reject) => {
    const thisDay = day ? day : '__ALL__';
    const req = http.request(`${deedSchedulerUrl}/deeds/${deed}?day=${thisDay}`,
        {method: 'DELETE'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    req.end();
  });
}


function listDeeds(day) {
  return new Promise((resolve, reject) => {
    const thisDay = day ? day : '__ALL__';
    const req = http.request(`${deedSchedulerUrl}/deeds?day=${thisDay}`,
        {method: 'GET'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    req.end();
  });
}


function updateDeed(deed, day, status) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${deedSchedulerUrl}/deeds/${deed}?day=${day}`,
        {method: 'PUT'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    const content = {status};
    req.setHeader('Content-Type', 'application/json');
    req.write(JSON.stringify(content));
    req.end();
  });
}


function viewDeed(deed, day, status) {
  return new Promise((resolve, reject) => {
    const thisDay = day ? day : '__ALL__';
    let thisStatus;
    if (status === 0) {
      thisStatus = status;
    } else {
      thisStatus = status ? status : '__ALL__';
    }
    const url = `${deedSchedulerUrl}/deeds/deed/${deed}?day=${thisDay}&status=${thisStatus}`;
    const req = http.request(url, {method: 'GET'}, (res) => {
      resolveData(res, resolve);
    });
    req.on('error', (e) => reject(e));
    req.end();
  });
}


module.exports = {addItem, deleteDeed, listDeeds,
  updateDeed, viewDeed};
