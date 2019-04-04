// Here we import the tracer before the mysql library
// to ensure that its mysql dependency is instrumented

const tracer = require('../tracer');
const mysql = require('mysql');

let pool;


function initDB() {
  const initSpan = tracer.startSpan('initDB');

  // Using the Scope's activate() function, we establish
  // initSpan as the active span for all asynchronous context
  // stemming from this anonymous function.
  tracer.scope().activate(initSpan, () => {
    pool = mysql.createPool({
      host: 'localhost',
      user: 'admin',
      database: 'mysql_db',
      password: 'password',
      waitForConnections: true,
      connectionLimit: 10,
      queueLimit: 0,
    });


    tracer.scope().activate(initSpan, () => {
      pool.getConnection((error, connection) => {
        if (error) {
          setTimeout(initDB, 5000);
          return console.error('error: ' + error.message);
        }

        const createTodos = `CREATE TABLE IF NOT EXISTS todos(
                       id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                       deed VARCHAR(255)NOT NULL,
                       note VARCHAR(255),
                       day VARCHAR (20) NOT NULL,
                       date TIMESTAMP NOT NULL DEFAULT current_timestamp,
                       completed TINYINT(1) NOT NULL DEFAULT 0
                      )`;


        connection.query(createTodos, (error, results, fields) => {
          if (error) {
            if (error.code === 'PROTOCOL_CONNECTION_LOST') {
              console.error('error.message: ' + error.message);
              initSpan.setTag('reconnecting', true);
              initDB();
            } else {
              initSpan.setTag('error', true);
              initSpan.log({'error': error.message});
              initSpan.finish();
              return console.error('error.message: ' + error.message);
            }
          }
          connection.release();
          if (error) {
            initSpan.log({'error': error.message});
            console.error('error.message: ' + error.message);
          }
        });
      });
    });
    initSpan.finish();
  });
}


async function addItem(deed, note, day) {
  const newItem = {
    deed: deed,
    note: note,
    day: day,
  };


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
  let sqlQuery;
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

initDB();
module.exports = {addItem, deleteDeed, listDeeds, updateDeed, viewDeed};
