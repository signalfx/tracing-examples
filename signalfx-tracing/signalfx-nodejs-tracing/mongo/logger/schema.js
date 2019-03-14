const mongoose = require('mongoose')

const Schema = mongoose.Schema

const SegmentSchema = new Schema({
  author: Schema.ObjectId,
  id: { type: String, index: true },
  body: String,
  date: { type: Date, default: Date.now }
})

module.exports = { SegmentSchema }
