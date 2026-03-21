function parseClaudeResponse(text) {
  try {
    return JSON.parse(text);
  } catch {
    // Fallback: extract JSON from response
    const match = text.match(/\{[\s\S]*\}/);
    if (match) {
      try {
        return JSON.parse(match[0]);
      } catch {
        throw new Error('Failed to parse AI response');
      }
    }
    throw new Error('No valid JSON found in AI response');
  }
}

function getModelForPlan(plan) {
  if (plan === 'pro' || plan === 'agency') {
    return 'claude-opus-4-20250115';
  }
  return 'claude-sonnet-4-20250514';
}

module.exports = { parseClaudeResponse, getModelForPlan };
