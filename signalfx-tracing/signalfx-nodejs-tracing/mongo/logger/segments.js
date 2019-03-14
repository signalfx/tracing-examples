// Here we import the tracer before the mongoose library
// to ensure that its mongodb-core dependency is instrumented
const tracer = require('./tracer')
const config = require('./config')
const { SegmentSchema } = require('./schema')

const mongoose = require('mongoose')

mongoose.connect(`${config.mongoUrl}/logs`, { useNewUrlParser: true })

const Segment = mongoose.model('Segment', SegmentSchema)

function newLogSegment (id, body) {
  return new Promise((resolve, reject) => {
    const segment = new Segment({ id, body })
    segment.save((err) => {
      if (err) {
        console.error(`Error saving segment: ${err}`)
        reject(err)
      }
      resolve(segment._id)
    })
  })
}

function getLogSegments (id) {
  return new Promise((resolve, reject) => {
    Segment.find({ id }, (err, segments) => {
      if (err) {
        return reject(err)
      }
      for (let i = 0, c = segments.length; i < c; i++) {
        const segment = segments[i]
        console.log(`Queried segment: ${segment._id} ${segment.id} ${segment.body} ${segment.date}`)
      }
      resolve(segments)
    })
  })
}

module.exports = { newLogSegment, getLogSegments }
