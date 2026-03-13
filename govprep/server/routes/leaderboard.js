const express = require('express');
const User = require('../models/User');
const auth = require('../middleware/auth');
const router = express.Router();

// GET /api/leaderboard/:examName
router.get('/:examName', auth, async (req, res) => {
  try {
    const users = await User.find({ targetExam: req.params.examName })
      .select('name xp streak badges')
      .sort({ xp: -1 })
      .limit(50);

    const leaderboard = users.map((u, i) => ({
      rank: i + 1,
      name: u.name,
      xp: u.xp,
      streak: u.streak.count,
      badges: u.badges,
      isCurrentUser: u._id.toString() === req.user.id
    }));

    res.json(leaderboard);
  } catch (err) {
    res.status(500).json({ msg: 'Server error' });
  }
});

// GET /api/badges
router.get('/', auth, async (req, res) => {
  try {
    const user = await User.findById(req.user.id).select('badges xp streak');
    if (!user) return res.status(404).json({ msg: 'User not found' });
    res.json({ badges: user.badges, xp: user.xp, streak: user.streak });
  } catch (err) {
    res.status(500).json({ msg: 'Server error' });
  }
});

module.exports = router;
