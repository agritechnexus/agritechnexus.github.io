const mongoose = require('mongoose');

const topicProgressSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  examName: String,
  subjects: [{
    name: String,
    topics: [{
      name: String,
      status: { type: String, enum: ['not_started', 'in_progress', 'revised', 'confident'], default: 'not_started' },
      lastUpdated: { type: Date, default: Date.now }
    }]
  }]
});

module.exports = mongoose.model('TopicProgress', topicProgressSchema);
