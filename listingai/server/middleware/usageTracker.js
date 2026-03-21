const PLAN_MONTHLY_LIMITS = {
  free: 5,
  starter: 100,
  pro: 500,
  agency: Infinity
};

const usageTracker = async (req, res, next) => {
  try {
    const user = req.user;
    const now = new Date();

    // Reset counter if past reset date
    if (user.usage.resetDate && now >= user.usage.resetDate) {
      user.usage.listingsThisMonth = 0;
      user.usage.resetDate = new Date(now.getFullYear(), now.getMonth() + 1, 1);
      await user.save();
    }

    const limit = PLAN_MONTHLY_LIMITS[user.plan] || 5;
    if (user.usage.listingsThisMonth >= limit) {
      return res.status(429).json({
        error: 'Monthly listing limit reached',
        limit,
        used: user.usage.listingsThisMonth,
        plan: user.plan,
        upgradeMessage: 'Upgrade your plan to generate more listings'
      });
    }

    next();
  } catch (err) {
    next(err);
  }
};

const incrementUsage = async (user, count = 1) => {
  user.usage.listingsThisMonth += count;
  await user.save();
};

module.exports = { usageTracker, incrementUsage, PLAN_MONTHLY_LIMITS };
