const mongoose = require('mongoose');

const listingSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  productName: { type: String, required: true },
  category: String,
  features: [String],
  targetAudience: String,
  priceRange: String,
  generatedListings: [{
    marketplace: String,
    language: String,
    title: String,
    bulletPoints: [String],
    description: String,
    searchTerms: String,
    metaDescription: String,
    seoScore: Number,
    seoTips: [String]
  }],
  status: { type: String, enum: ['generated', 'saved', 'exported'], default: 'generated' }
}, { timestamps: true });

listingSchema.index({ userId: 1, createdAt: -1 });

module.exports = mongoose.model('Listing', listingSchema);
