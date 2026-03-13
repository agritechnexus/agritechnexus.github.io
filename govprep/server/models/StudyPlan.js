const mongoose = require('mongoose');

const studyPlanSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  days: [{
    date: { type: Date, required: true },
    subjects: [String],
    status: { type: String, enum: ['done', 'missed', 'upcoming'], default: 'upcoming' }
  }],
  generatedAt: { type: Date, default: Date.now },
  lastAdjustedAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('StudyPlan', studyPlanSchema);
