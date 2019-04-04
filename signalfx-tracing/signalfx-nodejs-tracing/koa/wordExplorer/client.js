// http module auto-instrumentation will occur once the tracer is initialized,
// which occurs in the sourcing client script.
const http = require('http');
const {serverUrl} = require('./config');

const wordExplorerUrl = `${serverUrl}/wordExplorer`;

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

function addWord(word, usage='') {
  return new Promise((resolve, reject) => {
    const req = http.request(`${wordExplorerUrl}/add`,
        {method: 'POST'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    const content = {word, usage};
    req.setHeader('Content-Type', 'application/json');
    req.write(JSON.stringify(content));
    req.end();
  });
}

function deleteWord(word) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${wordExplorerUrl}/words/${word}`,
        {method: 'DELETE'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    req.end();
  });
}

function exploreWord(word) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${wordExplorerUrl}/explore/`,
        {method: 'POST'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    const content = {word};
    req.setHeader('Content-Type', 'application/json');
    req.write(JSON.stringify(content));
    req.end();
  });
}

function listWords() {
  return new Promise((resolve, reject) => {
    const req = http.request(`${wordExplorerUrl}/words`,
        {method: 'GET'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    req.end();
  });
}

function updateWord(word, usage) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${wordExplorerUrl}/words/${word}`,
        {method: 'PUT'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    const content = {usage};
    req.setHeader('Content-Type', 'application/json');
    req.write(JSON.stringify(content));
    req.end();
  });
}

function viewWord(findWord) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${wordExplorerUrl}/words/${findWord}`,
        {method: 'GET'}, (res) => {
          resolveData(res, resolve);
        });
    req.on('error', (e) => reject(e));
    req.end();
  });
}

module.exports = {addWord, deleteWord, exploreWord,
  listWords, updateWord, viewWord};
