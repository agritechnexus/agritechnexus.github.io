const csv = require('csv-parser');
const { Readable } = require('stream');
const ClaudeService = require('../services/claude.service');
const BulkJob = require('../models/BulkJob');
const Listing = require('../models/Listing');
const { incrementUsage } = require('../middleware/usageTracker');

exports.uploadBulk = async (req, res, next) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: 'CSV file is required' });
    }

    const { marketplace, language } = req.body;
    const products = [];

    await new Promise((resolve, reject) => {
      const stream = Readable.from(req.file.buffer.toString());
      stream
        .pipe(csv())
        .on('data', (row) => {
          if (row.productName || row.product_name) {
            products.push({
              productName: row.productName || row.product_name,
              category: row.category || 'General',
              features: (row.features || '').split(';').filter(Boolean),
              targetAudience: row.targetAudience || row.target_audience || '',
              priceRange: row.priceRange || row.price_range || ''
            });
          }
        })
        .on('end', resolve)
        .on('error', reject);
    });

    if (!products.length) {
      return res.status(400).json({ error: 'No valid products found in CSV' });
    }

    const job = await BulkJob.create({
      userId: req.userId,
      fileName: req.file.originalname,
      totalProducts: products.length,
      marketplace: marketplace || 'Amazon IN',
      language: language || 'English',
      status: 'processing'
    });

    // Process asynchronously
    processBulkJob(job, products, req.user).catch(err => {
      console.error('Bulk job error:', err);
    });

    res.status(201).json({ jobId: job._id, totalProducts: products.length });
  } catch (err) {
    next(err);
  }
};

async function processBulkJob(job, products, user) {
  try {
    const results = await ClaudeService.bulkGenerate(
      products,
      job.marketplace,
      job.language,
      user.plan
    );

    const jobResults = [];
    let successCount = 0;

    for (const result of results) {
      if (result.status === 'success') {
        const listing = await Listing.create({
          userId: user._id,
          productName: result.productName,
          generatedListings: [result.listing]
        });
        jobResults.push({ productName: result.productName, status: 'success', listingId: listing._id });
        successCount++;
      } else {
        jobResults.push({ productName: result.productName, status: 'failed', error: result.error });
      }
    }

    job.results = jobResults;
    job.processedCount = products.length;
    job.status = 'completed';
    job.completedAt = new Date();
    await job.save();

    await incrementUsage(user, successCount);
  } catch (err) {
    job.status = 'failed';
    await job.save();
  }
}

exports.getJobs = async (req, res, next) => {
  try {
    const jobs = await BulkJob.find({ userId: req.userId }).sort({ createdAt: -1 });
    res.json({ jobs });
  } catch (err) {
    next(err);
  }
};

exports.getJob = async (req, res, next) => {
  try {
    const job = await BulkJob.findOne({ _id: req.params.id, userId: req.userId });
    if (!job) return res.status(404).json({ error: 'Job not found' });
    res.json({ job });
  } catch (err) {
    next(err);
  }
};

exports.downloadJob = async (req, res, next) => {
  try {
    const job = await BulkJob.findOne({ _id: req.params.id, userId: req.userId }).populate('results.listingId');
    if (!job) return res.status(404).json({ error: 'Job not found' });
    if (job.status !== 'completed') return res.status(400).json({ error: 'Job not yet completed' });

    const listings = await Listing.find({
      _id: { $in: job.results.filter(r => r.listingId).map(r => r.listingId) }
    });

    const csvData = listings.map(l => {
      const gl = l.generatedListings[0];
      return {
        productName: l.productName,
        marketplace: gl?.marketplace || '',
        title: gl?.title || '',
        bulletPoints: gl?.bulletPoints?.join(' | ') || '',
        description: gl?.description || '',
        searchTerms: gl?.searchTerms || '',
        seoScore: gl?.seoScore || 0
      };
    });

    res.json({ data: csvData });
  } catch (err) {
    next(err);
  }
};
