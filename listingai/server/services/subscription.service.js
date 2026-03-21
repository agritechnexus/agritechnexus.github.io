const { PLAN_MONTHLY_LIMITS } = require('../middleware/usageTracker');

const PLAN_DETAILS = {
  free: { price: 0, name: 'Free', listings: 5, features: ['5 listings/month', 'Single marketplace', 'English only'] },
  starter: { price: 499, name: 'Starter', listings: 100, features: ['100 listings/month', 'All marketplaces', 'All languages', 'Email support'] },
  pro: { price: 1499, name: 'Pro', listings: 500, features: ['500 listings/month', 'All marketplaces', 'All languages', 'Bulk CSV upload', 'Image-to-listing', 'Priority AI model', 'Priority support'] },
  agency: { price: 4999, name: 'Agency', listings: Infinity, features: ['Unlimited listings', 'All marketplaces', 'All languages', 'Bulk CSV upload', 'Image-to-listing', 'Priority AI model', 'Dedicated support', 'API access'] }
};

class SubscriptionService {
  static getPlanDetails(plan) {
    return PLAN_DETAILS[plan] || PLAN_DETAILS.free;
  }

  static getAllPlans() {
    return PLAN_DETAILS;
  }

  static canAccessFeature(plan, feature) {
    const featureAccess = {
      bulkUpload: ['pro', 'agency'],
      imageToListing: ['pro', 'agency'],
      multiLanguage: ['starter', 'pro', 'agency'],
      multiMarketplace: ['starter', 'pro', 'agency'],
      apiAccess: ['agency']
    };
    return featureAccess[feature]?.includes(plan) || false;
  }
}

module.exports = SubscriptionService;
