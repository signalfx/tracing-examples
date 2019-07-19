const tracer = require('./tracer');

const pg = require('pg');
const pool = new pg.Pool({
  user: 'postgres',
  host: 'localhost',
  database: 'pg_db',
  password: 'password',
  port: '5432',
});
const createStatement = `CREATE TABLE IF NOT EXISTS contacts(
                         id SERIAL PRIMARY KEY,
                         firstName VARCHAR(40) NOT NULL,
                         lastName VARCHAR(40) NOT NULL,
                         email VARCHAR(50) NOT NULL UNIQUE)`;

function createTable() {
  const initSpan = tracer.startSpan('createTable');

  // Using the Scope's activate() function, we establish
  // initSpan as the active span for all asynchronous context
  // stemming from this anonymous function.

  tracer.scope().activate(initSpan, () => {
    pool.query(createStatement, (err, res) => {
      if (err) console.error(err);
      initSpan.finish();
    });
  });
}

async function addContact(fName, lName, email) {
  const response = await new Promise((resolve, reject) => {
    const sql = `INSERT INTO contacts(firstName, lastName, email)
    VALUES( '${fName}', '${lName}', '${email}')`;
    pool.query(sql,
        (err, res) => {
          if (err) {
            return reject(err);
          }
          resolve(res.rowCount);
        });
  });
  return response;
}

async function countRows(fName, lName) {
  const sql = `SELECT * FROM contacts
  WHERE LOWER(firstName) = LOWER('${fName}')
  AND LOWER(lastName) = LOWER('${lName}')`;
  const response = await new Promise((resolve, reject) => {
    pool.query(sql, (err, res) => {
      if (err) {
        return reject(err);
      }
      resolve(res.rowCount);
    });
  });
  return response;
}

async function deleteContact(fName, lName) {
  const response = await new Promise((resolve, reject) => {
    const sql = `DELETE FROM contacts
  WHERE LOWER(firstName) = LOWER('${fName}')
  AND LOWER(lastName) = LOWER('${lName}')`;
    pool.query(sql,
        (err, res) => {
          if (err) {
            return reject(err);
          }
          resolve(res.rowCount);
        });
  });
  return response;
}

async function deleteByID(id) {
  const response = await new Promise((resolve, reject) => {
    pool.query(`DELETE FROM contacts WHERE id = '${id}'`,
        (err, res) => {
          if (err) {
            return reject(err);
          }
          resolve(res.rowCount);
        });
  });
  return response;
}

async function getContact(fName, lName) {
  let sql;

  if (lName == '__ALL__') {
    sql = `SELECT * FROM contacts
    WHERE LOWER(firstName) = LOWER('${fName}')
    ORDER BY lastName ASC`;
  } else {
    sql = `SELECT * FROM contacts
    WHERE LOWER(firstName) = LOWER('${fName}')
    AND LOWER(lastName) = LOWER('${lName}')
    ORDER BY contacts.id ASC`;
  }

  const response = await new Promise((resolve, reject) => {
    pool.query(sql,
        (err, res) => {
          if (err) {
            return reject(err);
          }
          resolve(res.rows);
        });
  });
  return response;
}

async function listContacts() {
  const response = await new Promise((resolve, reject) => {
    const sql = `SELECT * FROM contacts GROUP BY (lastName, contacts.id)
                ORDER BY firstName ASC`;
    pool.query(sql, (err, res) => {
      if (err) {
        console.error(err);
      }
      resolve(res.rows);
    });
  });
  return response;
}

async function updateEmail(fName, lName, email) {
  const response = await new Promise((resolve, reject) => {
    const sql = `UPDATE contacts SET email = '${email}'
                WHERE LOWER(firstName) = LOWER('${fName}')
                AND LOWER(lastName) = LOWER('${lName}')`;
    pool.query(sql,
        (err, res) => {
          if (err) {
            return reject(err);
          }
          resolve(res.rowCount);
        });
  });
  return response;
}

async function updateByID(id, email) {
  const response = await new Promise((resolve, reject) => {
    pool.query(`UPDATE contacts SET email = '${email}' WHERE id = '${id}'`,
        (err, res) => {
          if (err) {
            return reject(err);
          }
          resolve(res.rowCount);
        });
  });
  return response;
}

createTable();
module.exports = {addContact, countRows, getContact,
  deleteByID, deleteContact, listContacts, updateByID,
  updateEmail};
