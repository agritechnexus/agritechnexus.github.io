const client = require('../config/claude');
const { SYSTEM_PROMPT, buildListingPrompt, buildImageAnalysisPrompt } = require('../utils/prompts');
const { parseClaudeResponse, getModelForPlan } = require('../utils/helpers');
const pLimit = require('p-limit');

const limit = pLimit(5);

class ClaudeService {
  static async generateListing(productData, marketplace, language, plan = 'free') {
    const model = getModelForPlan(plan);
    const prompt = buildListingPrompt(productData, marketplace, language);

    const response = await client.messages.create({
      model,
      max_tokens: 2000,
      system: SYSTEM_PROMPT,
      messages: [{ role: 'user', content: prompt }]
    });

    const text = response.content[0].text;
    const parsed = parseClaudeResponse(text);

    return {
      marketplace,
      language,
      title: parsed.title,
      bulletPoints: parsed.bulletPoints,
      description: parsed.description,
      searchTerms: parsed.searchTerms || '',
      metaDescription: parsed.metaDescription || '',
      seoScore: parsed.seoScore || 0,
      seoTips: parsed.seoTips || []
    };
  }

  static async generateListingStream(productData, marketplace, language, plan = 'free') {
    const model = getModelForPlan(plan);
    const prompt = buildListingPrompt(productData, marketplace, language);

    const stream = client.messages.stream({
      model,
      max_tokens: 2000,
      system: SYSTEM_PROMPT,
      messages: [{ role: 'user', content: prompt }]
    });

    return stream;
  }

  static async generateFromImage(imageBase64, marketplace, language, plan = 'free') {
    const model = getModelForPlan(plan);
    const prompt = buildImageAnalysisPrompt(marketplace, language);

    const mediaType = imageBase64.startsWith('/9j/') ? 'image/jpeg' : 'image/png';

    const response = await client.messages.create({
      model,
      max_tokens: 3000,
      system: SYSTEM_PROMPT,
      messages: [{
        role: 'user',
        content: [
          {
            type: 'image',
            source: { type: 'base64', media_type: mediaType, data: imageBase64 }
          },
          { type: 'text', text: prompt }
        ]
      }]
    });

    const text = response.content[0].text;
    const parsed = parseClaudeResponse(text);

    return {
      productAnalysis: parsed.productAnalysis,
      listing: {
        marketplace,
        language,
        ...parsed.listing
      }
    };
  }

  static async bulkGenerate(productsArray, marketplace, language, plan = 'free') {
    const results = await Promise.allSettled(
      productsArray.map(product =>
        limit(() => this.generateListing(product, marketplace, language, plan))
      )
    );

    return results.map((result, index) => ({
      productName: productsArray[index].productName,
      status: result.status === 'fulfilled' ? 'success' : 'failed',
      listing: result.status === 'fulfilled' ? result.value : null,
      error: result.status === 'rejected' ? result.reason.message : null
    }));
  }
}

module.exports = ClaudeService;
