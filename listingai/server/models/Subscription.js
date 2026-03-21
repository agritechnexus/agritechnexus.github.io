const mongoose = require('mongoose');

const subscriptionSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  planName: { type: String, enum: ['starter', 'pro', 'agency'], required: true },
  razorpaySubscriptionId: String,
  razorpayPaymentId: String,
  status: { type: String, enum: ['active', 'cancelled', 'expired'], default: 'active' },
  amount: Number,
  currency: { type: String, default: 'INR' },
  startDate: { type: Date, default: Date.now },
  endDate: Date
}, { timestamps: true });

module.exports = mongoose.model('Subscription', subscriptionSchema);
