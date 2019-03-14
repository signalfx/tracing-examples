// The core of the auto-instrumentation process is made in the
// tracer.js module.  It consists of a single import and invocation
// of the 'signalfx-tracing' module.
const tracer = require('./logger/tracer')

// Note http module import must come after init() that occurs
// in logger/tracer module import, as well as any modules that load it.
var http = require('http')
const { config, segments } = require('./logger')

// Obtain the reference to the tracer's Scope utility. This
// will allow activating new spans for in-process context propagation:
// https://github.com/signalfx/signalfx-nodejs-tracing/blob/master/docs/API.md
const scope = tracer.scope()

function handleGet (req, body) {
  // get id and query mongo for data, writing it to response
  return new Promise((resolve, reject) => {
    const id = req.url.substring(1)
    console.log(`Received id request for ${id}`)
    if (id === 'trigger_error') {
      const err = new Error('Error Triggered')
      req.destroy(err)
      return reject(err)
    }
    if (id.length !== 36) {
      body.error = 'You must specify a log id by path.'
      return resolve(400)
    }
    segments.getLogSegments(id).then((segments) => {
      let logs = ''
      if (segments.length === 0) {
        body.error = `No log content found for ${id}.`
        return resolve(404)
      }

      for (let i = 0, c = segments.length; i < c; i++) {
        const segment = segments[i]
        logs += `${segment.date}: ${segment.body}\n`
      }
      body.logs = logs
      resolve(200)
    }).catch((e) => reject(e))
  })
}

function handlePost (req, res) {
  // Create a new log segment for the request content
  return new Promise((resolve, reject) => {
    let parsed
    try {
      parsed = JSON.parse(req)
    } catch (e) {
      res.error = e.message
      resolve(400)
    }
    res.id = parsed.id
    const input = parsed.input
    segments.newLogSegment(parsed.id, input).then((_id) => {
      console.log(`handlePost: ${_id}`)
      res.DocumentId = _id
      resolve(200)
    }).catch((e) => reject(e))
  })
}

http.createServer((req, res) => {
  let data = ''
  req.on('data', (chunk) => {
    data += chunk.toString()
  })

  req.on('end', () => {
    console.log(data)
    if (req.method !== 'GET' && req.method !== 'POST') {
      res.writeHead(405, { 'Content-Type': 'text/plain' })
      res.write('Only "GET /<logId>" and "POST /" requests are accepted.')
      return res.end()
    }
    const response = {}
    const activeSpan = scope.active()
    if (req.method === 'GET') {
      // Manually create a span for the GET request handler and make it a child
      // of the automatically created http server's request span.
      const handleGetSpan = tracer.startSpan('handleGet', { childOf: activeSpan })
      // Activate the span via the Scope for the subsequent anonymous function.
      return scope.activate(handleGetSpan, () => {
        handleGet(req, response).then((statusCode) => {
          // Mark the GET request handler as having finished.
          handleGetSpan.finish()
          res.writeHead(statusCode, { 'Content-Type': 'application/json' })
          res.write(JSON.stringify(response))
          res.end()
        }).catch((e) => {
          handleGetSpan.log({ event: 'error', 'error.message': e.message })
          handleGetSpan.finish()
          console.error(`Error in handleGet: ${e.message}`)
          res.end()
        })
      })
    }
    // Manually create a span for the POST request handler and make it a child
    // of the automatically generated request span.
    const handlePostSpan = tracer.startSpan('handlePost', { childOf: activeSpan })
    scope.activate(handlePostSpan, () => {
      handlePost(data, response).then((statusCode) => {
        // Mark the POST request handler as having finished.
        handlePostSpan.finish()
        res.writeHead(statusCode, { 'Content-Type': 'application/json' })
        res.write(JSON.stringify(response))
        res.end()
      }).catch((e) => {
        handlePostSpan.log({ event: 'error', 'error.message': e.message })
        handlePostSpan.finish()
        console.error(`Error in handlePost: ${e.message}`)
        res.end()
      })
    })
  })

  req.on('error', (err) => {
    // Obtain the active span and log the error event.
    const span = scope.active()
    span.log({ event: 'error', 'error.message': err.message })
    console.error(err)
  })
}).listen(config.serverPort)
