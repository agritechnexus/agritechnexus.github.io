const SYSTEM_PROMPT = `You are ListingAI, an expert e-commerce copywriter who has written 100,000+ product listings for Indian marketplaces. You understand SEO, buyer psychology, and marketplace algorithms deeply.

Your writing style:
- Keyword-rich but natural-sounding
- Benefit-focused, not just feature-listing
- Uses power words that convert (Premium, Exclusive, Trusted, etc.)
- Follows each marketplace's specific character limits and formatting rules
- Culturally relevant for Indian buyers`;

const MARKETPLACE_RULES = {
  "Amazon IN": {
    titleMaxChars: 200,
    bulletPoints: 5,
    bulletMaxChars: 500,
    descriptionMaxChars: 2000,
    searchTermsMaxChars: 250,
    rules: "Title format: Brand + Product + Key Feature + Size/Quantity. No promotional text in title. No ALL CAPS. Backend search terms: no commas, no repeated words, no brand name."
  },
  "Flipkart": {
    titleMaxChars: 140,
    bulletPoints: 7,
    bulletMaxChars: 300,
    descriptionMaxChars: 4000,
    rules: "Title: concise, keyword-first. Highlights section with 7 key points. Description supports basic HTML. Include specifications table data."
  },
  "Meesho": {
    titleMaxChars: 100,
    bulletPoints: 4,
    descriptionMaxChars: 1000,
    rules: "Simple, straightforward language. Target Tier 2-3 city buyers. Mention value-for-money. Avoid English jargon, use simple words."
  },
  "Shopify": {
    titleMaxChars: 255,
    bulletPoints: 6,
    descriptionMaxChars: 5000,
    rules: "SEO-optimized for Google. Include meta description (160 chars). Rich HTML description with headers. Focus on brand storytelling."
  },
  "JioMart": {
    titleMaxChars: 150,
    bulletPoints: 5,
    bulletMaxChars: 300,
    descriptionMaxChars: 2000,
    rules: "Simple, clear language. Focus on value and daily essentials. Target mass-market Indian consumers. Include MRP and pack size details."
  }
};

function buildListingPrompt(productData, marketplace, language) {
  const rules = MARKETPLACE_RULES[marketplace];
  if (!rules) {
    throw new Error(`Unsupported marketplace: ${marketplace}`);
  }

  return `Generate a complete, SEO-optimized product listing for ${marketplace}.

PRODUCT DETAILS:
- Product Name: ${productData.productName}
- Category: ${productData.category}
- Key Features: ${productData.features.join(', ')}
${productData.targetAudience ? `- Target Audience: ${productData.targetAudience}` : ''}
${productData.priceRange ? `- Price Range: ${productData.priceRange}` : ''}

MARKETPLACE RULES:
${rules.rules}
- Title max: ${rules.titleMaxChars} characters
- Bullet points: ${rules.bulletPoints} points${rules.bulletMaxChars ? `, max ${rules.bulletMaxChars} chars each` : ''}
- Description max: ${rules.descriptionMaxChars} characters
${rules.searchTermsMaxChars ? `- Backend search terms: max ${rules.searchTermsMaxChars} characters` : ''}

LANGUAGE: Generate in ${language}. If Hindi/regional, use Roman script (Hinglish) for titles but Devanagari/native script option for descriptions.

RESPOND IN THIS EXACT JSON FORMAT (no markdown, no backticks):
{
  "title": "...",
  "bulletPoints": ["...", "...", "...", "...", "..."],
  "description": "...",
  "searchTerms": "...",
  "metaDescription": "...",
  "seoScore": 85,
  "seoTips": ["tip1", "tip2"]
}`;
}

function buildImageAnalysisPrompt(marketplace, language) {
  return `Analyze this product image and identify:
1. What the product is
2. Key visible features (material, color, size, design elements)
3. Likely category
4. Target audience
5. Estimated price range in INR

Then generate a complete, SEO-optimized product listing for ${marketplace} in ${language}.

RESPOND IN THIS EXACT JSON FORMAT (no markdown, no backticks):
{
  "productAnalysis": {
    "productName": "...",
    "category": "...",
    "features": ["...", "..."],
    "targetAudience": "...",
    "estimatedPriceRange": "..."
  },
  "listing": {
    "title": "...",
    "bulletPoints": ["...", "...", "...", "...", "..."],
    "description": "...",
    "searchTerms": "...",
    "metaDescription": "...",
    "seoScore": 85,
    "seoTips": ["tip1", "tip2"]
  }
}`;
}

module.exports = {
  SYSTEM_PROMPT,
  MARKETPLACE_RULES,
  buildListingPrompt,
  buildImageAnalysisPrompt
};
