const { serverUrl } = require('./config')

// http module auto-instrumentation will occur once the tracer is initialized,
// which occurs in the sourcing client script.
const http = require('http')

const snowmanUrl = `${serverUrl}/snowman`

// progress graphic
let snowman = ['        (\'_\')']
for (let i = 1; i < 8; i++) {
  snowman.push(`${' '.repeat(8 - i)}( ${'.'.repeat(i * 2 + 1)} )`)
}

function resolveData(res, resolve) {
  // resolves a Promise with parsed http response
  let data = '' 
  res.on('data', d => {
    data += d
  })
  res.on('end', () => {
    resolve(JSON.parse(data))
  })
}

function newGame() {
  return new Promise((resolve, reject) => {
    const req = http.request(`${snowmanUrl}/new`, { method: 'POST' }, (res) => {
      resolveData(res, resolve)
    })
    req.on('error', (e) => reject(e))
    req.end()
  })
}

function makeGuess(guess, id) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${snowmanUrl}/${id}`, { method: 'POST' }, res => {
      resolveData(res, resolve)
    })
    req.on('error', (e) => reject(e))

    const content = { guess }
    req.setHeader('Content-Type', 'application/json')
    req.write(JSON.stringify(content))
    req.end()
  })
}

function getAnswer(id) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${snowmanUrl}/${id}/answer`, { method: 'GET' }, res => {
      resolveData(res, resolve)
    })
    req.on('error', (e) => reject(e))
    req.end()
  })
}

function deleteGame(id) {
  return new Promise((resolve, reject) => {
    const req = http.request(`${snowmanUrl}/${id}`, { method: 'DELETE' }, res => {
      resolveData(res, resolve)
    })
    req.on('error', (e) => reject(e))
    req.end()
  })
}

module.exports = { snowman, newGame, makeGuess, getAnswer, deleteGame }