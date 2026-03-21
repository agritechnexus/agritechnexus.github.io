const User = require('../models/User');
const { PLAN_MONTHLY_LIMITS } = require('../middleware/usageTracker');

exports.getProfile = async (req, res) => {
  const user = req.user;
  const limit = PLAN_MONTHLY_LIMITS[user.plan] || 5;
  res.json({
    user,
    usage: {
      used: user.usage.listingsThisMonth,
      limit: limit === Infinity ? 'unlimited' : limit,
      resetDate: user.usage.resetDate
    }
  });
};

exports.updateProfile = async (req, res, next) => {
  try {
    const { name } = req.body;
    if (name) req.user.name = name;
    await req.user.save();
    res.json({ user: req.user });
  } catch (err) {
    next(err);
  }
};
