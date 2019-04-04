const MongoClient = require('mongoose');

const MONGO_URL = `mongodb://localhost/words_db`;
const WordSchema = new MongoClient.Schema({
  word: String,
  pos: String,
  meaning: String,
  synonyms: [String],
  usageNote: String,
});
const Word = MongoClient.model('Word', WordSchema);

const initDB = function() {
  MongoClient.connect(MONGO_URL, {useNewUrlParser: true})
      .then((connection) => {
        console.log('Database connection established\n');
      })
      .catch((err) => console.error(err));
};

function addWord(wordDict) {
  const newEntry = new Word({
    word: wordDict.word,
    pos: wordDict.pos,
    meaning: wordDict.meaning,
    synonyms: wordDict.synonyms,
    usageNote: wordDict.usageNote,
  });
  newEntry.save();
  return newEntry;
}

function deleteWord(word) {
  return Word.findOneAndRemove({'word': word});
}

function listWords() {
  return Word.find().sort({'word': 1});
}

function updateUsage(word, usageNote) {
  return Word.findOneAndUpdate({'word': word}, {$set: {usageNote}});
}

function viewWord(word) {
  return Word.findOne({'word': word});
}

initDB();

module.exports = {addWord, deleteWord, listWords, updateUsage, viewWord};
