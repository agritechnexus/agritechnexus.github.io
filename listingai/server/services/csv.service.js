const csv = require('csv-parser');
const { Readable } = require('stream');

class CsvService {
  static async parseProducts(buffer) {
    const products = [];
    return new Promise((resolve, reject) => {
      const stream = Readable.from(buffer.toString());
      stream
        .pipe(csv())
        .on('data', (row) => {
          const name = row.productName || row.product_name || row.name;
          if (name) {
            products.push({
              productName: name,
              category: row.category || 'General',
              features: (row.features || '').split(';').filter(Boolean),
              targetAudience: row.targetAudience || row.target_audience || '',
              priceRange: row.priceRange || row.price_range || ''
            });
          }
        })
        .on('end', () => resolve(products))
        .on('error', reject);
    });
  }

  static generateCsv(listings) {
    const headers = ['Product Name', 'Marketplace', 'Title', 'Bullet Points', 'Description', 'Search Terms', 'SEO Score'];
    const rows = listings.map(l => {
      const gl = l.generatedListings?.[0];
      return [
        l.productName,
        gl?.marketplace || '',
        `"${(gl?.title || '').replace(/"/g, '""')}"`,
        `"${(gl?.bulletPoints || []).join(' | ').replace(/"/g, '""')}"`,
        `"${(gl?.description || '').replace(/"/g, '""')}"`,
        `"${(gl?.searchTerms || '').replace(/"/g, '""')}"`,
        gl?.seoScore || 0
      ].join(',');
    });

    return [headers.join(','), ...rows].join('\n');
  }
}

module.exports = CsvService;
