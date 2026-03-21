const ClaudeService = require('../services/claude.service');
const Listing = require('../models/Listing');
const { validateListingInput } = require('../utils/validators');
const { incrementUsage } = require('../middleware/usageTracker');

exports.generateListing = async (req, res, next) => {
  try {
    const { productName, category, features, targetAudience, priceRange, marketplace, language } = req.body;
    const errors = validateListingInput({ productName, category, features, marketplace, language });
    if (errors.length) {
      return res.status(400).json({ errors });
    }

    const productData = { productName, category, features, targetAudience, priceRange };
    const result = await ClaudeService.generateListing(
      productData,
      marketplace || 'Amazon IN',
      language || 'English',
      req.user.plan
    );

    const listing = await Listing.create({
      userId: req.userId,
      productName,
      category,
      features,
      targetAudience,
      priceRange,
      generatedListings: [result]
    });

    await incrementUsage(req.user);

    res.status(201).json({ listing });
  } catch (err) {
    next(err);
  }
};

exports.generateMulti = async (req, res, next) => {
  try {
    const { productName, category, features, targetAudience, priceRange, marketplaces, language } = req.body;
    if (!marketplaces?.length) {
      return res.status(400).json({ error: 'At least one marketplace is required' });
    }

    const productData = { productName, category, features, targetAudience, priceRange };
    const results = await Promise.all(
      marketplaces.map(mp =>
        ClaudeService.generateListing(productData, mp, language || 'English', req.user.plan)
      )
    );

    const listing = await Listing.create({
      userId: req.userId,
      productName,
      category,
      features,
      targetAudience,
      priceRange,
      generatedListings: results
    });

    await incrementUsage(req.user, marketplaces.length);

    res.status(201).json({ listing });
  } catch (err) {
    next(err);
  }
};

exports.generateFromImage = async (req, res, next) => {
  try {
    const { marketplace, language } = req.body;
    if (!req.file) {
      return res.status(400).json({ error: 'Image file is required' });
    }

    const imageBase64 = req.file.buffer.toString('base64');
    const result = await ClaudeService.generateFromImage(
      imageBase64,
      marketplace || 'Amazon IN',
      language || 'English',
      req.user.plan
    );

    const listing = await Listing.create({
      userId: req.userId,
      productName: result.productAnalysis.productName,
      category: result.productAnalysis.category,
      features: result.productAnalysis.features,
      generatedListings: [result.listing]
    });

    await incrementUsage(req.user);

    res.status(201).json({ listing, productAnalysis: result.productAnalysis });
  } catch (err) {
    next(err);
  }
};

exports.generateDemo = async (req, res, next) => {
  try {
    const { productName, category, features, marketplace, language } = req.body;
    const errors = validateListingInput({ productName, category, features, marketplace, language });
    if (errors.length) {
      return res.status(400).json({ errors });
    }

    const productData = { productName, category, features };
    const result = await ClaudeService.generateListing(
      productData,
      marketplace || 'Amazon IN',
      language || 'English',
      'free'
    );

    res.json({ listing: result });
  } catch (err) {
    next(err);
  }
};

exports.getHistory = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    const skip = (page - 1) * limit;

    const [listings, total] = await Promise.all([
      Listing.find({ userId: req.userId }).sort({ createdAt: -1 }).skip(skip).limit(limit),
      Listing.countDocuments({ userId: req.userId })
    ]);

    res.json({ listings, total, page, pages: Math.ceil(total / limit) });
  } catch (err) {
    next(err);
  }
};

exports.getListing = async (req, res, next) => {
  try {
    const listing = await Listing.findOne({ _id: req.params.id, userId: req.userId });
    if (!listing) return res.status(404).json({ error: 'Listing not found' });
    res.json({ listing });
  } catch (err) {
    next(err);
  }
};

exports.deleteListing = async (req, res, next) => {
  try {
    const listing = await Listing.findOneAndDelete({ _id: req.params.id, userId: req.userId });
    if (!listing) return res.status(404).json({ error: 'Listing not found' });
    res.json({ message: 'Listing deleted' });
  } catch (err) {
    next(err);
  }
};

exports.exportListing = async (req, res, next) => {
  try {
    const listing = await Listing.findOne({ _id: req.params.id, userId: req.userId });
    if (!listing) return res.status(404).json({ error: 'Listing not found' });

    listing.status = 'exported';
    await listing.save();

    const exportData = listing.generatedListings.map(gl => ({
      marketplace: gl.marketplace,
      title: gl.title,
      bulletPoints: gl.bulletPoints.join(' | '),
      description: gl.description,
      searchTerms: gl.searchTerms,
      seoScore: gl.seoScore
    }));

    res.json({ exportData });
  } catch (err) {
    next(err);
  }
};
