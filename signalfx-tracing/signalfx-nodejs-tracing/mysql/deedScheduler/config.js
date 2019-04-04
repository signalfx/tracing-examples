const serverPort = 3001;
const driver = process.env['DEEDSCHEDULER_MYSQL_CLIENT'] || '2';
let mysqlLib;

if (Number(driver) === 1) {
  mysqlLib = 'MySQL';
} else {
  mysqlLib = 'MySQL2';
};


module.exports = {
  mysqlLib,
  driver,
  serverPort,
  'serverUrl': `http://localhost:${serverPort}`,
};
