const tracer = require('./tracer');
const myDB = require('./mongoDriver');
const WordNet = require('node-wordnet');

const wordnet = new WordNet();


async function lookUpWord(word) {
  const output = {'word': 'n/a',
    'pos': 'n/a',
    'meaning': 'n/a',
    'synonyms': 'n/a',
    'usageNote': 'n/a'};
  const results = await wordnet.lookupAsync(word);
  if (results[0]) {
    let usage = '';
    const shortdef = results[0].gloss.split(';');
    if (shortdef[1]) {
      usage = shortdef[1].replace(/\"/g, '').replace(/\"/g, '');
    }
    output.word = word;
    output.pos = results[0].pos;
    output.meaning = shortdef[0];
    output.synonyms = results[0].synonyms;
    output.usageNote = usage;
  };
  return output;
}

async function add(word, usage) {
  const span = tracer.scope().active();
  let response;
  const results = await lookUpWord(word);

  if (results.word !== 'n/a') {
    span.setTag('addedWord', word);
    results.usageNote = usage ? usage : results.usageNote;
    response = await myDB.addWord(results);
  } else {
    response = results;
  }
  return response;
}

async function addWord(word, usage='') {
  let response;
  const retrieved = await myDB.viewWord(word);

  if (retrieved) {
    response = 'EXISTS';
  } else {
    response = await add(word, usage);
  }
  return response;
}

async function deleteWord(word) {
  return await myDB.deleteWord(word);
}

async function exploreWord(word) {
  const span = tracer.scope().active();
  span.setTag('exploredWord', word);
  return await lookUpWord(word);
}

async function listWords() {
  const span = tracer.scope().active();
  span.setTag('listWord', 'True');
  return await myDB.listWords();
}

async function updateUsage(word, usage) {
  const span = tracer.scope().active();
  let response;

  const retrieved = await viewWord(word);
  if (retrieved) {
    span.setTag('updatedWord', word);
    response = await myDB.updateUsage(word, usage);
  } else {
    response = await add(word, usage);
  }
  return response;
}

async function viewWord(word) {
  const span = tracer.scope().active();
  span.setTag('viewedWord', word);
  return await myDB.viewWord(word);
}

module.exports = {addWord, deleteWord, exploreWord,
  listWords, updateUsage, viewWord};
