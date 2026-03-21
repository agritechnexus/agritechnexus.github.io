const { MARKETPLACE_RULES } = require('../utils/prompts');

class MarketplaceService {
  static getMarketplaceRules(marketplace) {
    return MARKETPLACE_RULES[marketplace] || null;
  }

  static getSupportedMarketplaces() {
    return Object.keys(MARKETPLACE_RULES);
  }

  static validateListingForMarketplace(listing, marketplace) {
    const rules = MARKETPLACE_RULES[marketplace];
    if (!rules) return { valid: false, errors: ['Unknown marketplace'] };

    const errors = [];
    if (listing.title?.length > rules.titleMaxChars) {
      errors.push(`Title exceeds ${rules.titleMaxChars} character limit`);
    }
    if (listing.bulletPoints?.length !== rules.bulletPoints) {
      errors.push(`Expected ${rules.bulletPoints} bullet points`);
    }
    if (listing.description?.length > rules.descriptionMaxChars) {
      errors.push(`Description exceeds ${rules.descriptionMaxChars} character limit`);
    }

    return { valid: errors.length === 0, errors };
  }
}

module.exports = MarketplaceService;
