const StudyPlan = require('../models/StudyPlan');

/**
 * Generate a weighted study plan based on user's exam date and subject ratings.
 * Weak subjects get more frequency, strong subjects get less.
 */
async function generatePlan(user) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const examDate = new Date(user.examDate);
  examDate.setHours(0, 0, 0, 0);

  const totalDays = Math.max(1, Math.ceil((examDate - today) / (1000 * 60 * 60 * 24)));

  // Weight subjects: weak=3, medium=2, strong=1
  const weights = { weak: 3, medium: 2, strong: 1 };
  const subjectPool = [];
  for (const s of user.subjects) {
    const w = weights[s.selfRating] || 2;
    for (let i = 0; i < w; i++) {
      subjectPool.push(s.name);
    }
  }

  // Assign 2-3 subjects per day, cycling through the weighted pool
  const days = [];
  let poolIndex = 0;
  for (let d = 0; d < totalDays; d++) {
    const date = new Date(today);
    date.setDate(today.getDate() + d);

    const subjectsPerDay = d % 3 === 0 ? 3 : 2;
    const daySubjects = new Set();
    for (let s = 0; s < subjectsPerDay; s++) {
      daySubjects.add(subjectPool[poolIndex % subjectPool.length]);
      poolIndex++;
    }

    days.push({
      date,
      subjects: Array.from(daySubjects),
      status: d === 0 ? 'upcoming' : 'upcoming'
    });
  }

  // Remove old plan and save new one
  await StudyPlan.findOneAndDelete({ userId: user._id });
  const plan = await StudyPlan.create({
    userId: user._id,
    days,
    generatedAt: new Date(),
    lastAdjustedAt: new Date()
  });

  return plan;
}

/**
 * Redistribute remaining topics when a day is missed.
 */
async function redistributePlan(userId) {
  const plan = await StudyPlan.findOne({ userId });
  if (!plan) return null;

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  // Collect missed subjects
  const missedSubjects = [];
  for (const day of plan.days) {
    if (day.status === 'missed') {
      missedSubjects.push(...day.subjects);
    }
  }

  if (missedSubjects.length === 0) return plan;

  // Find upcoming days and distribute missed subjects
  const upcomingDays = plan.days.filter(d => d.status === 'upcoming' && new Date(d.date) >= today);
  let idx = 0;
  for (const subject of missedSubjects) {
    if (upcomingDays.length > 0) {
      const targetDay = upcomingDays[idx % upcomingDays.length];
      if (!targetDay.subjects.includes(subject)) {
        targetDay.subjects.push(subject);
      }
      idx++;
    }
  }

  plan.lastAdjustedAt = new Date();
  await plan.save();
  return plan;
}

module.exports = { generatePlan, redistributePlan };
