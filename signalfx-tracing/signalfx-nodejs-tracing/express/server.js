// The core of the auto-instrumentation process is made in the
// tracer.js module.  It consists of a single import and invocation
// of the 'signalfx-tracing' module.
const tracer = require('./snowman/tracer')

// Note express module import must come after init() that occurs
// in snowman/tracer module import, as well as any modules that load it.
const express = require('express')
const bodyParser = require('body-parser')
const snowman = require('./snowman')

const app = express()
app.use(bodyParser.json())
app.use('/snowman', snowman.router)

app.listen(snowman.config.serverPort)
