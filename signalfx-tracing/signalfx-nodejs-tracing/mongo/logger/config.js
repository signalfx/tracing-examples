const serverPort = 8080
const mongoPort = 27017

module.exports = {
  serverPort,
  serverUrl: `http://localhost:${serverPort}`,
  mongoPort,
  mongoUrl: `mongodb://localhost:${mongoPort}`
}
