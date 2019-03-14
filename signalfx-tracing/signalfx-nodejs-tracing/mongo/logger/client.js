// Here we import the tracer before the request library
// to ensure that its http dependency is instrumented
const tracer = require('./tracer')
const config = require('./config')

const readline = require('readline')
const request = require('request')
const uuid = require('uuid/v1')

const id = uuid()

function getLog (id) {
  // obtains the log from the logger server
  return new Promise((resolve, reject) => {
    // Here we create a new span to track the input submission
    const querySpan = tracer.startSpan('GetLog')
    querySpan.setTag('id', id)
    // Using the Scope's activate() function, we establish
    // querySpan as the active span for all asynchronous context
    // stemming from this anonymous function.
    tracer.scope().activate(querySpan, () => {
      request({ method: 'GET', uri: `${config.serverUrl}/${id}` },
        (error, response, body) => {
          if (error) {
            console.error(error)
            // Set some error metadata and log the occurrence for our span
            querySpan.setTag('error', true)
            querySpan.log({ 'error.message': error.message })
            querySpan.finish()
            return reject(error)
          }
          let parsed
          try {
            parsed = JSON.parse(body)
          } catch (e) {
            console.error(e)
            querySpan.setTag('error', true)
            querySpan.log({ 'error.message': error.message })
            querySpan.finish()
            return reject(e)
          }
          if (response.statusCode !== 200) {
            return resolve(parsed)
          }
          // Explicitly finish the span
          querySpan.finish()
          resolve(parsed.logs)
      })
    })
  })
}

function logPrompt () {
  return new Promise((resolve, reject) => {
    // Initializes the readline session and sends the
    // logger server all new lines as individual segments
    const rl = readline.createInterface({
      input: process.stdin,
      output: process.stdout
    })

    rl.setPrompt('log ~ ')

    let sentLog = false
    rl.on('close', (e) => {
      rl.setPrompt('')
      if (sentLog) {
        console.log(`Log saved: ${id}`)
        resolve()
      }
      console.log('Goodbye!')
    })

    rl.on('line', (input) => {
      if (!input) {
        return rl.prompt()
      } else if (input === '/q') {
        return rl.close()
      }
      // Here we create a new span to track the input submission
      const inputSpan = tracer.startSpan('SendLog')
      inputSpan.setTag('input', input)
      // establish inputStpan as the active span for asynchronous context
      // stemming from this anonymous function.  In this case doing so will
      // cause the auto-generated http request span to be a child of inputSpan
      tracer.scope().activate(inputSpan, () => {
        request({ method: 'POST', uri: config.serverUrl, json: { id, input } },
          (error, response, body) => {
            // Obtain the auto-instrumented POST span
            if (error) {
              console.error(error)
              // Set the error metadata and log its occurrence for the parent span
              inputSpan.setTag('error', true)
              inputSpan.log({ 'error.message': error.message })
              inputSpan.finish()
              return rl.close()
            }
            sentLog = true
            inputSpan.finish()
            rl.prompt()
          })
      })
    })
    rl.prompt()
  })
}

module.exports = { getLog, logPrompt }
