const VALID_MARKETPLACES = ['Amazon IN', 'Flipkart', 'Meesho', 'Shopify', 'JioMart'];
const VALID_LANGUAGES = ['English', 'Hindi', 'Telugu', 'Tamil', 'Kannada', 'Bengali', 'Marathi'];
const VALID_CATEGORIES = [
  'Electronics', 'Fashion', 'Home & Kitchen', 'Beauty', 'Sports',
  'Toys', 'Books', 'Food', 'Auto', 'Health'
];

function validateListingInput(data) {
  const errors = [];
  if (!data.productName?.trim()) errors.push('Product name is required');
  if (!data.category?.trim()) errors.push('Category is required');
  if (!data.features?.length) errors.push('At least one feature is required');
  if (data.features?.length > 10) errors.push('Maximum 10 features allowed');
  if (data.marketplace && !VALID_MARKETPLACES.includes(data.marketplace)) {
    errors.push(`Invalid marketplace. Valid: ${VALID_MARKETPLACES.join(', ')}`);
  }
  if (data.language && !VALID_LANGUAGES.includes(data.language)) {
    errors.push(`Invalid language. Valid: ${VALID_LANGUAGES.join(', ')}`);
  }
  return errors;
}

function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

module.exports = { validateListingInput, validateEmail, VALID_MARKETPLACES, VALID_LANGUAGES, VALID_CATEGORIES };
