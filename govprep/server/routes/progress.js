const express = require('express');
const TopicProgress = require('../models/TopicProgress');
const User = require('../models/User');
const auth = require('../middleware/auth');
const router = express.Router();

// GET /api/progress
router.get('/', auth, async (req, res) => {
  try {
    const progress = await TopicProgress.findOne({ userId: req.user.id });
    if (!progress) return res.status(404).json({ msg: 'No progress data found' });
    res.json(progress);
  } catch (err) {
    res.status(500).json({ msg: 'Server error' });
  }
});

// PATCH /api/progress/:subject/:topic
router.patch('/:subject/:topic', auth, async (req, res) => {
  try {
    const { status } = req.body;
    if (!['not_started', 'in_progress', 'revised', 'confident'].includes(status)) {
      return res.status(400).json({ msg: 'Invalid status' });
    }

    const progress = await TopicProgress.findOne({ userId: req.user.id });
    if (!progress) return res.status(404).json({ msg: 'No progress data found' });

    const subject = progress.subjects.find(s => s.name === req.params.subject);
    if (!subject) return res.status(404).json({ msg: 'Subject not found' });

    const topic = subject.topics.find(t => t.name === req.params.topic);
    if (!topic) return res.status(404).json({ msg: 'Topic not found' });

    topic.status = status;
    topic.lastUpdated = new Date();
    await progress.save();

    // Award XP for updating progress
    const user = await User.findById(req.user.id);
    user.xp += 20;

    // Check coverage badges
    const allTopics = progress.subjects.flatMap(s => s.topics);
    const total = allTopics.length;
    const covered = allTopics.filter(t => t.status === 'revised' || t.status === 'confident').length;
    const coveragePercent = total > 0 ? (covered / total) * 100 : 0;

    if (coveragePercent >= 50 && !user.badges.includes('50% Covered')) {
      user.badges.push('50% Covered');
    }
    if (coveragePercent >= 100 && !user.badges.includes('100% Covered')) {
      user.badges.push('100% Covered');
    }

    await user.save();

    res.json(progress);
  } catch (err) {
    console.error(err);
    res.status(500).json({ msg: 'Server error' });
  }
});

module.exports = router;
