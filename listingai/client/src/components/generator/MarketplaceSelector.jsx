const MARKETPLACES = [
  { id: 'Amazon IN', label: 'Amazon IN', color: 'text-orange-400' },
  { id: 'Flipkart', label: 'Flipkart', color: 'text-blue-400' },
  { id: 'Meesho', label: 'Meesho', color: 'text-pink-400' },
  { id: 'Shopify', label: 'Shopify', color: 'text-green-400' },
  { id: 'JioMart', label: 'JioMart', color: 'text-indigo-400' },
];

export default function MarketplaceSelector({ selected, onChange, multi = false }) {
  const toggle = (id) => {
    if (multi) {
      const next = selected.includes(id) ? selected.filter(s => s !== id) : [...selected, id];
      onChange(next);
    } else {
      onChange(id);
    }
  };

  return (
    <div>
      <label className="block text-sm font-medium text-dark-200 mb-2">
        {multi ? 'Select Marketplaces' : 'Marketplace'}
      </label>
      <div className="flex flex-wrap gap-2">
        {MARKETPLACES.map(({ id, label, color }) => {
          const isActive = multi ? selected.includes(id) : selected === id;
          return (
            <button
              key={id}
              type="button"
              onClick={() => toggle(id)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-all border ${
                isActive
                  ? 'bg-primary/10 border-primary text-primary'
                  : 'bg-dark-800 border-dark-600 text-dark-300 hover:border-dark-400'
              }`}
            >
              {label}
            </button>
          );
        })}
      </div>
    </div>
  );
}
