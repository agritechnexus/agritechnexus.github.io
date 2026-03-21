const rateLimit = require('express-rate-limit');

const PLAN_LIMITS = {
  free: { windowMs: 60000, max: 2 },
  starter: { windowMs: 60000, max: 10 },
  pro: { windowMs: 60000, max: 30 },
  agency: { windowMs: 60000, max: 60 }
};

const planBasedLimiter = rateLimit({
  windowMs: 60000,
  max: (req) => {
    const plan = req.user?.plan || 'free';
    return PLAN_LIMITS[plan]?.max || 2;
  },
  keyGenerator: (req) => req.user?._id?.toString() || req.ip,
  message: { error: 'Too many requests. Please try again later or upgrade your plan.' },
  standardHeaders: true,
  legacyHeaders: false
});

module.exports = { planBasedLimiter };
