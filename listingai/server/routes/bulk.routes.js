const router = require('express').Router();
const multer = require('multer');
const { auth, requirePlan } = require('../middleware/auth');
const { usageTracker } = require('../middleware/usageTracker');
const { uploadBulk, getJobs, getJob, downloadJob } = require('../controllers/bulk.controller');

const upload = multer({ storage: multer.memoryStorage(), limits: { fileSize: 5 * 1024 * 1024 } });

router.post('/upload', auth, requirePlan('pro', 'agency'), usageTracker, upload.single('file'), uploadBulk);
router.get('/jobs', auth, getJobs);
router.get('/jobs/:id', auth, getJob);
router.get('/jobs/:id/download', auth, downloadJob);

module.exports = router;
