import { useState } from 'react';
import { Wand2, X } from 'lucide-react';
import Button from '../common/Button';
import Input from '../common/Input';
import MarketplaceSelector from './MarketplaceSelector';
import LanguageSelector from './LanguageSelector';

const CATEGORIES = [
  'Electronics', 'Fashion', 'Home & Kitchen', 'Beauty', 'Sports',
  'Toys', 'Books', 'Food', 'Auto', 'Health'
];

export default function SingleListingForm({ onSubmit, loading }) {
  const [form, setForm] = useState({
    productName: '',
    category: 'Electronics',
    features: [],
    targetAudience: '',
    priceRange: '',
    marketplaces: ['Amazon IN'],
    language: 'English',
  });
  const [featureInput, setFeatureInput] = useState('');

  const addFeature = (e) => {
    if (e.key === 'Enter' && featureInput.trim() && form.features.length < 10) {
      e.preventDefault();
      setForm(f => ({ ...f, features: [...f.features, featureInput.trim()] }));
      setFeatureInput('');
    }
  };

  const removeFeature = (index) => {
    setForm(f => ({ ...f, features: f.features.filter((_, i) => i !== index) }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.productName.trim() || !form.features.length) return;
    onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <Input
        label="Product Name *"
        placeholder="e.g., Premium Stainless Steel Water Bottle 1L"
        value={form.productName}
        onChange={e => setForm(f => ({ ...f, productName: e.target.value }))}
        required
      />

      <div>
        <label className="block text-sm font-medium text-dark-200 mb-1.5">Category *</label>
        <select
          value={form.category}
          onChange={e => setForm(f => ({ ...f, category: e.target.value }))}
          className="input-field"
        >
          {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-dark-200 mb-1.5">
          Key Features * <span className="text-dark-500">({form.features.length}/10)</span>
        </label>
        <div className="flex flex-wrap gap-2 mb-2">
          {form.features.map((f, i) => (
            <span key={i} className="inline-flex items-center gap-1 bg-primary/10 text-primary border border-primary/20 rounded-full px-3 py-1 text-sm">
              {f}
              <button type="button" onClick={() => removeFeature(i)}><X size={14} /></button>
            </span>
          ))}
        </div>
        <input
          className="input-field"
          placeholder="Type a feature and press Enter"
          value={featureInput}
          onChange={e => setFeatureInput(e.target.value)}
          onKeyDown={addFeature}
          disabled={form.features.length >= 10}
        />
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Input
          label="Target Audience"
          placeholder="e.g., Health-conscious millennials"
          value={form.targetAudience}
          onChange={e => setForm(f => ({ ...f, targetAudience: e.target.value }))}
        />
        <Input
          label="Price Range"
          placeholder="e.g., Rs 500-800"
          value={form.priceRange}
          onChange={e => setForm(f => ({ ...f, priceRange: e.target.value }))}
        />
      </div>

      <MarketplaceSelector
        selected={form.marketplaces}
        onChange={marketplaces => setForm(f => ({ ...f, marketplaces }))}
        multi
      />

      <LanguageSelector
        value={form.language}
        onChange={language => setForm(f => ({ ...f, language }))}
      />

      <Button type="submit" loading={loading} className="w-full text-lg py-4">
        <Wand2 size={20} /> Generate Listing
      </Button>
    </form>
  );
}
