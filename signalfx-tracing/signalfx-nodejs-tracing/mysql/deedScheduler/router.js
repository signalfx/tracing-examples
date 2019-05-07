// Here we import the tracer before all the other libraries
// to ensure that the various dependencies are instrumented

const tracer = require('./tracer');

const BodyParser = require('koa-bodyparser');
const Router = require('koa-router');
const scheduler = require('./scheduler');


const router = new Router();


async function addItem(ctx) {
  const span = tracer.scope().active();

  const deed = ctx.request.body.deed;
  const note = ctx.request.body.note;
  const day = ctx.request.body.day;

  span.setTag('added', deed);
  await scheduler.addItem(deed, note, day)
      .then((response) => {
        ctx.body = {
          message: `You just added '${deed}' on ${day} to your deedScheduler!
Number of rows added: ${response}`,
        };
      })
      .catch((error) => {
        ctx.body = {message: `ERROR: ${error}`};
      });
}


async function deleteDeed(ctx) {
  const day = ctx.query.day;
  const deed = ctx.params.deed;

  const span = tracer.scope().active();
  if (day === '__ALL__') {
    span.setTag(deed, 'all');
  } else {
    span.setTag(deed, day);
  }


  await scheduler.deleteDeed(deed, day)
      .then((response) => {
        if (response > 0) {
          if (day === '__ALL__') {
            ctx.body = {message: `${deed} is no longer in your deedScheduler!
Number of rows affected: ${response}`};
          } else {
            ctx.body = {message:
                      `${deed} on ${day} is no longer in your deedScheduler!
Number of rows affected: ${response}`};
          }
        } else {
          if (day === '__ALL__') {
            ctx.body = {
              message:
              `You have no ${deed} in your deedScheduler.`,
            };
          } else {
            ctx.body = {
              message:
              `You have no ${deed} on ${day} in your deedScheduler.`,
            };
          }
        }
      })
      .catch((error) => {
        ctx.body = {message: `ERROR: ${error}`};
      });
}


async function listDeeds(ctx) {
  const span = tracer.scope().active();

  const day = ctx.query.day;

  span.setTag('deedsList', true);
  await scheduler.listDeeds(day)
      .then((response) => {
        if (response[0]) {
          ctx.body = response;
        } else {
          ctx.body = {message: `No entry was found to match your query`};
        }
      })
      .catch((error) => {
        ctx.body = {message: `ERROR: ${error}`};
      });
}


async function updateDeed(ctx) {
  const span = tracer.scope().active();

  const deed = ctx.params.deed;
  const day = ctx.query.day;
  const status = ctx.request.body.status;

  await scheduler.updateDeed(deed, day, status)
      .then((response) => {
        if (response.affectedRows > 0) {
          span.setTag('updated_deed', deed);
          ctx.body = {message:
                 `You have successfully updated ${deed}.
Number of rows changed: ${response.affectedRows}`};
        } else {
          ctx.body = {message:
                 `No entry matching ${deed} on ${day} was found to update`};
        }
        span.setTag('rows affected', response.affectedRows);
      })
      .catch((error) => {
        ctx.body = {message: `ERROR: ${error}`};
      });
}


async function viewDeed(ctx) {
  const span = tracer.scope().active();

  const deed = ctx.params.deed;
  const day = ctx.query.day;
  const status = ctx.query.status;

  await scheduler.viewDeed(deed, day, status)
      .then((response) => {
        if (response[0]) {
          ctx.body = response;
          span.setTag('viewed', deed);
        } else {
          ctx.body = {message: `No entry was found to match your query`};
        }
      })
      .catch((error) => {
        ctx.body = {message: `ERROR: ${error}`};
      });
}


router
    .use(BodyParser())
    .get('/deeds', listDeeds)
    .get('/deeds/deed/:deed', viewDeed)
    .post('/add', addItem)
    .put('/deeds/:deed', updateDeed)
    .delete('/deeds/:deed', deleteDeed);

module.exports = router;
