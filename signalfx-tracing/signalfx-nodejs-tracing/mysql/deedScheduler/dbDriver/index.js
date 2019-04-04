const dbOption = require('../config').driver;

let driverLib;

if (dbOption == 1) {
  driverLib = require('../dbDriver/mysql_driver');
} else {
  driverLib = require('../dbDriver/mysql2_driver');
}

module.exports = driverLib;
