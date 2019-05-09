const tracer = require('../tracer');
const poolConfig = {host: 'localhost',
  user: 'admin',
  database: 'mysql_db',
  password: 'password',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
};


function initDB(pool) {
  const initSpan = tracer.startSpan('initDB');

  // Using the Scope's activate() function, we establish
  // initSpan as the active span for all asynchronous context
  // stemming from this anonymous function.
  tracer.scope().activate(initSpan, () => {
    pool.getConnection((error, connection) => {
      if (error) {
        setTimeout(initDB, 5000);
        initSpan.setTag('error', true);
        initSpan.log({'error': error.message});
        initSpan.finish();
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
            initSpan.log({'error': error.message});
            setTimeout(initDB, 5000);
          } else {
            initSpan.setTag('error', true);
            initSpan.log({'error': error.message});
            console.error('error.message: ' + error.message);
          }
        }
        try {
          connection.release();
        } catch (error) {
          initSpan.setTag('error', true);
          initSpan.log({'error': error.message});
          console.error('error.message: ' + error.message);
        }
        initSpan.finish();
      });
    });
  });
}

module.exports = {initDB, poolConfig};
