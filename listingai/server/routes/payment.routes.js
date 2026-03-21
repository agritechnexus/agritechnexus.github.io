const router = require('express').Router();
const { auth } = require('../middleware/auth');
const { createOrder, verifyPayment, webhook, getSubscription } = require('../controllers/payment.controller');

router.post('/create-order', auth, createOrder);
router.post('/verify', auth, verifyPayment);
router.post('/webhook', webhook);
router.get('/subscription', auth, getSubscription);

module.exports = router;
