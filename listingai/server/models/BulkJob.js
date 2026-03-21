const mongoose = require('mongoose');

const bulkJobSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  fileName: String,
  totalProducts: { type: Number, required: true },
  processedCount: { type: Number, default: 0 },
  marketplace: String,
  language: { type: String, default: 'English' },
  status: { type: String, enum: ['queued', 'processing', 'completed', 'failed'], default: 'queued' },
  results: [{
    productName: String,
    status: { type: String, enum: ['success', 'failed'] },
    listingId: { type: mongoose.Schema.Types.ObjectId, ref: 'Listing' },
    error: String
  }],
  completedAt: Date
}, { timestamps: true });

module.exports = mongoose.model('BulkJob', bulkJobSchema);
