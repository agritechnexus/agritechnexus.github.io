const router = require('express').Router();
const multer = require('multer');
const { auth } = require('../middleware/auth');
const { planBasedLimiter } = require('../middleware/rateLimit');
const { usageTracker } = require('../middleware/usageTracker');
const {
  generateListing, generateMulti, generateFromImage, generateDemo,
  getHistory, getListing, deleteListing, exportListing
} = require('../controllers/listing.controller');

const upload = multer({ storage: multer.memoryStorage(), limits: { fileSize: 10 * 1024 * 1024 } });

// Demo endpoint (no auth)
router.post('/demo', generateDemo);

// Protected routes
router.post('/generate', auth, planBasedLimiter, usageTracker, generateListing);
router.post('/generate-multi', auth, planBasedLimiter, usageTracker, generateMulti);
router.post('/from-image', auth, planBasedLimiter, usageTracker, upload.single('image'), generateFromImage);
router.get('/history', auth, getHistory);
router.get('/:id', auth, getListing);
router.delete('/:id', auth, deleteListing);
router.post('/:id/export', auth, exportListing);

module.exports = router;
