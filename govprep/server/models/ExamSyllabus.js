const mongoose = require('mongoose');

const examSyllabusSchema = new mongoose.Schema({
  examName: { type: String, required: true, unique: true },
  subjects: [{
    name: String,
    topics: [String]
  }]
});

module.exports = mongoose.model('ExamSyllabus', examSyllabusSchema);
