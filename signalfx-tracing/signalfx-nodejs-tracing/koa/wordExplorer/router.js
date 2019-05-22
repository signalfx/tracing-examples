const tracer = require('./tracer');
const BodyParser = require('koa-bodyparser');
const Router = require('koa-router');
const Explorer = require('./explorer');

const router = new Router();
const explorer = Explorer;


async function addWord(ctx) {
  const span = tracer.scope().active();
  const word = ctx.request.body.word;
  const usage = ctx.request.body.usage;

  span.setTag('addedWord', word);
  const response = await explorer.addWord(word, usage);
  if (response === 'EXISTS') {
    ctx.body = {message: `'${word}' is already part of your vocabulary list.
You may update '${word}' with a new usage note.`,
    };
  } else if (response.word !== 'n/a') {
    ctx.body = {message: response};
  } else {
    ctx.body = {message: `Yikes! '${word}' not found!!
Check spelling or try adding a different word.`,
    };
  }
}

async function deleteWord(ctx) {
  const word = ctx.params.word;
  let msg;

  const response = await explorer.deleteWord(word);
  if (response) {
    msg = `'${response.word}' has been removed from your vocabulary list.`;
  } else {
    msg = `'${ctx.params.word}' was not found in your vocabulary.`;
  }
  ctx.body = {message: msg};
}

async function exploreWord(ctx) {
  const span = tracer.scope().active();
  const word = ctx.request.body.word;

  span.setTag('exploredWord', word);
  const results = await explorer.exploreWord(word);
  if (results.word !== 'n/a') {
    ctx.body = {message: results};
  } else {
    ctx.body = {message: `ERROR: '${word}' not found.
Check spelling or try another word.`};
  }
  ctx.respond = results;
}

async function listWords(ctx) {
  const span = tracer.scope().active();

  span.setTag('listWord', true);
  const retrievedResponse = await explorer.listWords();
  if (retrievedResponse) {
    ctx.body = {message: retrievedResponse};
  } else {
    ctx.body = {message: 'No list found.'};
  }
}

async function updateUsage(ctx) {
  const span = tracer.scope().active();
  const word = ctx.params.word;
  const usage = ctx.request.body.usage;

  const retrievedResponse = await explorer.updateUsage(word, usage);
  if (retrievedResponse) {
    span.setTag('updatedWord', word);
    ctx.body = {message: `${retrievedResponse.word} has been updated.`};
  } else {
    const msg = `Yikes! '${word}' not found!!
Check spelling or try a different word.`;
    ctx.body = {message: msg};
  }
}

async function viewWord(ctx) {
  const span = tracer.scope().active();
  const word = ctx.params.word;

  span.setTag('viewedWord', word);
  const retrievedResponse = await explorer.viewWord(word);
  if (retrievedResponse) {
    ctx.body = {message: retrievedResponse};
  } else {
    ctx.status = 404;
    const msg = `ERROR: '${word}' not found.
Check spelling, try another word or add '${word}' to your vocab list.`;
    ctx.body = {message: msg};
  }
}

router
    .use(BodyParser())
    .get('/words', listWords)
    .get('/words/:word', viewWord)
    .post('/explore', exploreWord)
    .post('/add', addWord)
    .put('/words/:word', updateUsage)
    .delete('/words/:word', deleteWord);

module.exports = router;
