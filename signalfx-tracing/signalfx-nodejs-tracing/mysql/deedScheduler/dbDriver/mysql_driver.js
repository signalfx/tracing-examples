const tracer = require('../tracer');
const mysql = require('mysql');
const config = require('./dbConfig');

const pool = mysql.createPool(config.poolConfig);


async function addItem(deed, note, day) {
  const newItem = {deed: deed, note: note, day: day};
  const response = await new Promise((resolve, reject) =>{
    pool.query('INSERT INTO todos SET ?', newItem, (error, results)=> {
      if (error) {
        console.log(error);
        reject(error);
      } else {
        const res = results.affectedRows;
        resolve(res);
      }
    });
  });
  return response;
}

async function deleteDeed(deed, day) {
  let sqlQuery;

  if (day === '__ALL__') {
    sqlQuery = `DELETE FROM todos WHERE deed = '${deed}'`;
  } else {
    sqlQuery = `DELETE FROM todos WHERE deed = '${deed}'
                 AND day = '${day}' `;
  }

  const response = await new Promise((resolve, reject) => {
    pool.query(sqlQuery,
        (error, results, fields) => {
          if (error) {
            reject(error);
          } else {
            const res = results.affectedRows;
            resolve(res);
          }
        });
  });
  return response;
}

async function listDeeds(day) {
  const span = tracer.scope().active();
  let sqlQuery;

  span.setTag('db_list', true);
  if (day === '__ALL__') {
    sqlQuery = 'SELECT deed FROM todos';
  } else {
    sqlQuery = `SELECT * FROM todos WHERE day = '${day}'`;
  }

  const response = await new Promise((resolve, reject) => {
    pool.query(sqlQuery, (error, results) => {
      if (error) {
        reject(error);
      } else {
        resolve(results);
      }
    });
  });
  return response;
}

async function updateDeed(deed, day, status) {
  const sql = `UPDATE todos
                    SET completed = ?
                    WHERE deed = ?
                    AND day = ?`;
  const data = [status, deed, day];
  const response = await new Promise((resolve, reject) => {
    pool.query(sql, data, (error, results, fields) => {
      if (error) {
        reject(error);
      } else {
        resolve(results);
      }
    });
  });
  return response;
}

async function viewDeed(deed, day, status) {
  let sqlQuery;

  if (day === '__ALL__') {
    sqlQuery = `SELECT * FROM todos WHERE deed = '${deed}' `;
  } else if (status === '__ALL__') {
    sqlQuery = `SELECT * FROM todos
                  WHERE deed = '${deed}'
                  AND day = '${day}' `;
  } else {
    sqlQuery = `SELECT * FROM todos
                  WHERE deed = '${deed}'
                  AND day = '${day}'
                  AND completed = '${status}' `;
  }

  const response = await new Promise((resolve, reject) => {
    pool.query(sqlQuery, (error, results, fields) => {
      if (error) {
        throw (error);
      } else {
        resolve(results);
      }
    });
  });
  return response;
}

config.initDB(pool);

module.exports = {addItem, deleteDeed, listDeeds, updateDeed, viewDeed};
