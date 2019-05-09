const tracer = require('./tracer');
const myDB = require('./dbDriver');

function addItem(deed, note, day) {
  const span = tracer.scope().active();
  span.setTag('addedDeed', deed);

  return myDB.addItem(deed, note, day);
}

function deleteDeed(deed, day) {
  const span = tracer.scope().active();
  span.setTag('deletedDeed', deed);

  return myDB.deleteDeed(deed, day);
}

function listDeeds(day) {
  const span = tracer.scope().active();
  span.setTag('listDeed', true);

  return myDB.listDeeds(day);
}

function updateDeed(deed, day, status) {
  const span = tracer.scope().active();
  span.setTag('updatedDeed', deed);

  return myDB.updateDeed(deed, day, status);
}

function viewDeed(deed, day, status) {
  const span = tracer.scope().active();
  span.setTag('viewedDeed', deed);

  return myDB.viewDeed(deed, day, status);
}

module.exports = {addItem, deleteDeed, listDeeds,
  updateDeed, viewDeed};
