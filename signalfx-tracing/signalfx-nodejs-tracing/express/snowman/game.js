const words = require('./words')

function getLetterIndices(letter, word) {
  // Returns an array of indices for a given letter in a word, if any
  let indices = []
  let index = word.indexOf(letter)
  while (index !== -1) {
    indices.push(index)
    index = index !== -1 ? word.indexOf(letter, index + 1) : -1 
  }
  return indices
}

function replaceAtIndices(letter, indices, word) {
  // Replaces characters of a word at given indices with a specified letter
  let replaced = word
  indices.forEach((index) => {
    replaced = replaced.substr(0, index) + letter + replaced.substr(index + 1)
  })
  return replaced
}

const games = {}

function getGame(id) {
  const game = games[id]
  if (!game) {
    const error = new Error(`Game ${id} not found.`)
    error.statusCode = 404
    throw error
  }
  return game
}

function deleteGame(id) {
  getGame(id) // ensure that game exists
  delete games[id]
}

class Game {
  constructor(id) {
    games[id] = this
    this.id = id
    this.word = this.randomWord()
    this.progress = '_'.repeat(this.word.length)
    this.remainingMisses = 8
    this.guesses = []
  }

  toString() {
    return JSON.stringify({
      id: this.id,
      word: this.word,
      progress: this.progress,
      remainingMisses: this.remainingMisses,
      guesses: this.guesses  
    })
  }

  randomWord() {
    const randomAddr = Math.floor(Math.random() * words.length)
    return words[randomAddr]
  }

  guess(letter) {
    let err
    if (!letter || letter.length !== 1) {
      err = new Error(`Guess must be a single letter.  Received "${letter}."`)
      err.statusCode = 400
      throw err
    }

    if (this.guesses.indexOf(letter) !== -1) {
      err = new Error(`You have already guessed "${letter}": [${this.guesses}].`)
      err.statusCode = 405
      err.remainingMisses = this.remainingMisses
      throw err
    }

    const indices = getLetterIndices(letter, this.word)
    this.guesses.push(letter)

    if (indices.length === 0) {
      this.remainingMisses -= 1
      if (this.remainingMisses === 0) {
        err = new Error(`You Lost!  The answer was "${this.word}."  Deleting game.`)
        delete games[this.id]
      } else {
        err = new Error(`There is no "${letter}" in the word.`)
        err.guesses = this.guesses
        err.progress = this.progress
      }

      err.statusCode = 404
      err.remainingMisses = this.remainingMisses

      throw err
    }
    this.progress = replaceAtIndices(letter, indices, this.progress) 
  }
}


module.exports = { Game, getGame, deleteGame }
