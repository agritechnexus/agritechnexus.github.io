const express = require('express');
const StudyPlan = require('../models/StudyPlan');
const User = require('../models/User');
const auth = require('../middleware/auth');
const { generatePlan, redistributePlan } = require('./planUtils');
const router = express.Router();

// GET /api/plan
router.get('/', auth, async (req, res) => {
  try {
    const plan = await StudyPlan.findOne({ userId: req.user.id });
    if (!plan) return res.status(404).json({ msg: 'No plan found' });

    // Auto-mark past upcoming days as missed
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    let changed = false;
    for (const day of plan.days) {
      const dayDate = new Date(day.date);
      dayDate.setHours(0, 0, 0, 0);
      if (dayDate < today && day.status === 'upcoming') {
        day.status = 'missed';
        changed = true;
      }
    }
    if (changed) await plan.save();

    res.json(plan);
  } catch (err) {
    res.status(500).json({ msg: 'Server error' });
  }
});

// PATCH /api/plan/:date — mark day done/missed or adjust subjects
router.patch('/:date', auth, async (req, res) => {
  try {
    const plan = await StudyPlan.findOne({ userId: req.user.id });
    if (!plan) return res.status(404).json({ msg: 'No plan found' });

    const targetDate = new Date(req.params.date);
    targetDate.setHours(0, 0, 0, 0);

    const day = plan.days.find(d => {
      const dd = new Date(d.date);
      dd.setHours(0, 0, 0, 0);
      return dd.getTime() === targetDate.getTime();
    });

    if (!day) return res.status(404).json({ msg: 'Day not found in plan' });

    if (req.body.status) day.status = req.body.status;
    if (req.body.subjects) day.subjects = req.body.subjects;

    // Handle gamification for marking done
    if (req.body.status === 'done') {
      const user = await User.findById(req.user.id);
      const allDaySubjectsDone = true; // marking entire day as done

      // XP: +10 per subject, +50 bonus for full day
      user.xp += day.subjects.length * 10 + 50;

      // Streak
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const lastActive = user.streak.lastActiveDate ? new Date(user.streak.lastActiveDate) : null;
      if (lastActive) {
        lastActive.setHours(0, 0, 0, 0);
        const diffDays = Math.floor((today - lastActive) / (1000 * 60 * 60 * 24));
        if (diffDays === 1) {
          user.streak.count += 1;
        } else if (diffDays > 1) {
          user.streak.count = 1;
        }
      } else {
        user.streak.count = 1;
      }
      user.streak.lastActiveDate = today;

      // Streak badges
      if (user.streak.count >= 7 && !user.badges.includes('7-Day Streak')) {
        user.badges.push('7-Day Streak');
        user.xp += 100;
      }
      if (user.streak.count >= 30 && !user.badges.includes('30-Day Streak')) {
        user.badges.push('30-Day Streak');
        user.xp += 100;
      }

      await user.save();
    }

    // If missed, redistribute
    if (req.body.status === 'missed') {
      await plan.save();
      await redistributePlan(req.user.id);
      const updatedPlan = await StudyPlan.findOne({ userId: req.user.id });
      return res.json(updatedPlan);
    }

    plan.lastAdjustedAt = new Date();
    await plan.save();
    res.json(plan);
  } catch (err) {
    console.error(err);
    res.status(500).json({ msg: 'Server error' });
  }
});

// POST /api/plan/regenerate
router.post('/regenerate', auth, async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    if (!user) return res.status(404).json({ msg: 'User not found' });
    const plan = await generatePlan(user);
    res.json(plan);
  } catch (err) {
    res.status(500).json({ msg: 'Server error' });
  }
});

module.exports = router;
