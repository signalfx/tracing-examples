const tracer = require('./tracer');

const myDB = require('./dbDriver');


async function addItem(deed, note, day) {
  const span = tracer.scope().active();
  span.setTag('addedDeed', deed);

  return myDB.addItem(deed, note, day);
}


async function deleteDeed(deed, day) {
  const span = tracer.scope().active();
  span.setTag('deletedDeed', deed);

  return myDB.deleteDeed(deed, day);
}


async function listDeeds(day) {
  const span = tracer.scope().active();
  span.setTag('listDeed', true);

  return await myDB.listDeeds(day);
}


async function updateDeed(deed, day, status) {
  const span = tracer.scope().active();
  span.setTag('updatedDeed', deed);

  return await myDB.updateDeed(deed, day, status);
}


async function viewDeed(deed, day, status) {
  const span = tracer.scope().active();
  span.setTag('viewedDeed', deed);

  return myDB.viewDeed(deed, day, status);
}


module.exports = {addItem, deleteDeed, listDeeds,
  updateDeed, viewDeed};
