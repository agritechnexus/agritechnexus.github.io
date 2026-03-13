const express = require('express');
const User = require('../models/User');
const ExamSyllabus = require('../models/ExamSyllabus');
const StudyPlan = require('../models/StudyPlan');
const TopicProgress = require('../models/TopicProgress');
const auth = require('../middleware/auth');
const { generatePlan } = require('./planUtils');
const router = express.Router();

// POST /api/onboarding
router.post('/', auth, async (req, res) => {
  try {
    const { targetExam, examDate, subjects } = req.body;
    if (!targetExam || !examDate || !subjects) {
      return res.status(400).json({ msg: 'Missing required fields' });
    }

    const user = await User.findById(req.user.id);
    if (!user) return res.status(404).json({ msg: 'User not found' });

    user.targetExam = targetExam;
    user.examDate = new Date(examDate);
    user.subjects = subjects;
    user.onboarded = true;
    await user.save();

    // Generate study plan
    const plan = await generatePlan(user);

    // Initialize topic progress
    const exam = await ExamSyllabus.findOne({ examName: targetExam });
    if (exam) {
      await TopicProgress.findOneAndDelete({ userId: user._id });
      await TopicProgress.create({
        userId: user._id,
        examName: targetExam,
        subjects: exam.subjects.map(s => ({
          name: s.name,
          topics: s.topics.map(t => ({ name: t, status: 'not_started', lastUpdated: new Date() }))
        }))
      });
    }

    res.json({ msg: 'Onboarding complete', plan });
  } catch (err) {
    console.error(err);
    res.status(500).json({ msg: 'Server error' });
  }
});

module.exports = router;
