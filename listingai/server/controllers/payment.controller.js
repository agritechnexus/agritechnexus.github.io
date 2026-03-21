const crypto = require('crypto');
const razorpay = require('../config/razorpay');
const User = require('../models/User');
const Subscription = require('../models/Subscription');

const PLANS = {
  starter: { amount: 49900, name: 'Starter', currency: 'INR' },
  pro: { amount: 149900, name: 'Pro', currency: 'INR' },
  agency: { amount: 499900, name: 'Agency', currency: 'INR' }
};

exports.createOrder = async (req, res, next) => {
  try {
    const { planName } = req.body;
    if (!PLANS[planName]) {
      return res.status(400).json({ error: 'Invalid plan' });
    }

    if (!razorpay) {
      return res.status(503).json({ error: 'Payment service not configured' });
    }

    const plan = PLANS[planName];
    const order = await razorpay.orders.create({
      amount: plan.amount,
      currency: plan.currency,
      receipt: `order_${req.userId}_${Date.now()}`,
      notes: { userId: req.userId.toString(), planName }
    });

    res.json({ order, key: process.env.RAZORPAY_KEY_ID });
  } catch (err) {
    next(err);
  }
};

exports.verifyPayment = async (req, res, next) => {
  try {
    const { razorpay_order_id, razorpay_payment_id, razorpay_signature, planName } = req.body;

    const expectedSignature = crypto
      .createHmac('sha256', process.env.RAZORPAY_KEY_SECRET)
      .update(`${razorpay_order_id}|${razorpay_payment_id}`)
      .digest('hex');

    if (expectedSignature !== razorpay_signature) {
      return res.status(400).json({ error: 'Payment verification failed' });
    }

    const plan = PLANS[planName];
    const endDate = new Date();
    endDate.setMonth(endDate.getMonth() + 1);

    await Subscription.create({
      userId: req.userId,
      planName,
      razorpayPaymentId: razorpay_payment_id,
      amount: plan.amount / 100,
      status: 'active',
      endDate
    });

    req.user.plan = planName;
    req.user.subscription = {
      razorpayId: razorpay_payment_id,
      status: 'active',
      currentPeriodEnd: endDate
    };
    await req.user.save();

    res.json({ message: 'Payment verified, plan upgraded', plan: planName });
  } catch (err) {
    next(err);
  }
};

exports.webhook = async (req, res) => {
  // Razorpay webhook handling
  const secret = process.env.RAZORPAY_KEY_SECRET;
  const signature = req.headers['x-razorpay-signature'];

  if (secret && signature) {
    const expectedSignature = crypto
      .createHmac('sha256', secret)
      .update(JSON.stringify(req.body))
      .digest('hex');

    if (expectedSignature !== signature) {
      return res.status(400).json({ error: 'Invalid webhook signature' });
    }
  }

  const event = req.body.event;
  if (event === 'payment.captured') {
    // Payment was successful — already handled in verify
  } else if (event === 'subscription.cancelled') {
    const paymentId = req.body.payload?.subscription?.entity?.id;
    if (paymentId) {
      const user = await User.findOne({ 'subscription.razorpayId': paymentId });
      if (user) {
        user.plan = 'free';
        user.subscription.status = 'cancelled';
        await user.save();
      }
    }
  }

  res.json({ status: 'ok' });
};

exports.getSubscription = async (req, res) => {
  const subscription = await Subscription.findOne({ userId: req.userId, status: 'active' }).sort({ createdAt: -1 });
  res.json({ subscription, plan: req.user.plan });
};
