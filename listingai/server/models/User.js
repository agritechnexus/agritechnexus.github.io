const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const userSchema = new mongoose.Schema({
  email: { type: String, required: true, unique: true, lowercase: true, trim: true },
  password: { type: String, required: true, minlength: 6 },
  name: { type: String, required: true, trim: true },
  plan: { type: String, enum: ['free', 'starter', 'pro', 'agency'], default: 'free' },
  usage: {
    listingsThisMonth: { type: Number, default: 0 },
    resetDate: { type: Date, default: () => {
      const now = new Date();
      return new Date(now.getFullYear(), now.getMonth() + 1, 1);
    }}
  },
  subscription: {
    razorpayId: String,
    status: String,
    currentPeriodEnd: Date
  }
}, { timestamps: true });

userSchema.pre('save', async function(next) {
  if (!this.isModified('password')) return next();
  this.password = await bcrypt.hash(this.password, 12);
  next();
});

userSchema.methods.comparePassword = async function(candidatePassword) {
  return bcrypt.compare(candidatePassword, this.password);
};

userSchema.methods.toJSON = function() {
  const obj = this.toObject();
  delete obj.password;
  return obj;
};

module.exports = mongoose.model('User', userSchema);
