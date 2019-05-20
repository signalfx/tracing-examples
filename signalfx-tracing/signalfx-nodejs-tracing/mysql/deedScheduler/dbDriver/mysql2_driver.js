const tracer = require('../tracer');
const mysql2 = require('mysql2');
const config = require('./dbConfig');

const pool = mysql2.createPool(config.poolConfig);
const poolPromise = pool.promise();


async function addItem(deed, note, day) {
  let response;
  const newItem = {deed: deed, note: note, day: day};

  await poolPromise.query('INSERT INTO todos SET ?', newItem)
      .then( ([results, fields]) => {
        response = results.affectedRows;
      })
      .catch(console.log);
  return response;
}

async function deleteDeed(deed, day) {
  let response;
  let sqlQuery;

  if (day === '__ALL__') {
    sqlQuery = `DELETE FROM todos WHERE deed = '${deed}'`;
  } else {
    sqlQuery = `DELETE FROM todos WHERE deed = '${deed}'
                   AND day = '${day}' `;
  }

  await poolPromise.query(sqlQuery)
      .then( ([results, fields]) => {
        response = results.affectedRows;
      })
      .catch(console.log);
  return response;
}

async function listDeeds(day) {
  const span = tracer.scope().active();
  let response;
  let sqlQuery;

  span.setTag('db_list', true);
  if (day === '__ALL__') {
    sqlQuery = 'SELECT deed FROM todos';
  } else {
    sqlQuery = `SELECT * FROM todos WHERE day = '${day}'`;
  }

  await poolPromise.query(sqlQuery)
      .then( ([results, fields]) => {
        response = results;
      })
      .catch(console.log);
  return response;
}

async function updateDeed(deed, day, status) {
  const sql = `UPDATE todos
                SET completed = ?
                WHERE deed = ?
                AND day = ?`;
  const data = [status, deed, day];
  let response;

  await poolPromise.query(sql, data)
      .then( ([results, fields]) => {
        response = results;
      })
      .catch(console.log);
  return response;
}

async function viewDeed(deed, day, status) {
  let response;
  let sqlQuery;

  if (day === '__ALL__') {
    sqlQuery = `SELECT * FROM todos WHERE deed = '${deed}' `;
  } else if (status === '__ALL__') {
    sqlQuery = `SELECT * FROM todos WHERE deed = '${deed}' AND day = '${day}' `;
  } else {
    sqlQuery = `SELECT * FROM todos WHERE deed = '${deed}'
                AND day = '${day}' AND completed = '${status}' `;
  }

  await poolPromise.query(sqlQuery)
      .then( ([results, fields]) => {
        response = results;
      })
      .catch(console.log);
  return response;
}

config.initDB(pool);

module.exports = {addItem, deleteDeed, listDeeds, updateDeed, viewDeed};
