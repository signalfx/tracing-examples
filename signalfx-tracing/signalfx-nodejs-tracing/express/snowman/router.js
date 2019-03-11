const tracer = require('./tracer')
const express = require('express')
const uuid = require('uuid/v4')
const { Game, getGame, deleteGame } = require('./game')

const router = express.Router()

function newGameRequest(req, res) {
  const span = tracer.scope().active()
  const game = new Game(uuid())
  span.setTag('id', game.id)
  span.setTag('word', game.word)
  res.status(201).json({
    id: game.id,
    progress: game.progress,
    remainingGuess: game.remainingMisses,
    message: 'Game successfully created.'
  })
}
router.post('/new', newGameRequest)

function deleteGameRequest(req, res) {
  const id = req.params.id
  deleteGame(id)
  res.status(200).json({
    id,
    message: `Successfully deleted game ${id}.`
  })
}
router.delete('/:id', deleteGameRequest)

function guessRequest(req, res) {
  const span = tracer.scope().active()
  const id = req.params.id
  span.setTag('id', id)
  const game = getGame(id)

  span.setTag('guess', req.body.guess)
  game.guess(req.body.guess)

  span.setTag('remainingMisses', game.remainingMisses)
  span.setTag('progress', game.progress)

  let responseBody = {}
  if (game.progress === game.word) {
    responseBody.message = `You've guessed "${game.word}" correctly!  Deleting game.`
    deleteGame(id)
  } else {
    responseBody = {
      message: 'You found a match!',
      progress:  game.progress,
      remainingMisses: game.remainingMisses,
      guesses: game.guesses
    }
  }
  res.status(200).json(responseBody)
}
router.post('/:id', guessRequest)

function answerRequest(req, res) {
  const id = req.params.id
  const game = getGame(id)
  res.status(200).json({ message: `The answer was: ${game.word}.  Deleting game.`})
  deleteGame(id)
}
router.get('/:id/answer', answerRequest)

function errorViewHandler(err, req, res, next) {
  // Formats property-loaded errors before sending as response,
  // passing error to main error handler otherwise.
  if (!err.statusCode) {
    return next(err)
  }

  const responseBody = {
    error: true,
    message: err.toString() 
  }

  if (err.remainingMisses !== undefined) {
    responseBody.remainingMisses = err.remainingMisses
  }

  const properties = ['guesses', 'progress']
  properties.forEach((prop) => {
    if (err[prop]) {
      responseBody[prop] = err[prop]
    }
  })

  res.status(err.statusCode).json(responseBody)
}
router.use(errorViewHandler)

module.exports = router
