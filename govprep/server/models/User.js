const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  targetExam: { type: String, default: '' },
  examDate: { type: Date },
  subjects: [{
    name: String,
    selfRating: { type: String, enum: ['strong', 'medium', 'weak'], default: 'medium' }
  }],
  streak: {
    count: { type: Number, default: 0 },
    lastActiveDate: { type: Date }
  },
  xp: { type: Number, default: 0 },
  badges: [String],
  onboarded: { type: Boolean, default: false },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('User', userSchema);
