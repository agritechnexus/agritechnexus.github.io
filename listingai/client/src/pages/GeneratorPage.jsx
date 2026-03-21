import { useState } from 'react';
import { Wand2, ImageIcon } from 'lucide-react';
import SingleListingForm from '../components/generator/SingleListingForm';
import ImageToListing from '../components/generator/ImageToListing';
import ListingOutput from '../components/generator/ListingOutput';
import { useListings } from '../hooks/useListings';
import { useAuth } from '../hooks/useAuth';
import toast from 'react-hot-toast';

export default function GeneratorPage() {
  const { generateListing, generateMulti, loading } = useListings();
  const { user } = useAuth();
  const [result, setResult] = useState(null);
  const [tab, setTab] = useState('form');
  const [lastForm, setLastForm] = useState(null);

  const handleGenerate = async (form) => {
    setLastForm(form);
    try {
      let listing;
      if (form.marketplaces.length > 1) {
        listing = await generateMulti({
          productName: form.productName,
          category: form.category,
          features: form.features,
          targetAudience: form.targetAudience,
          priceRange: form.priceRange,
          marketplaces: form.marketplaces,
          language: form.language
        });
      } else {
        listing = await generateListing({
          productName: form.productName,
          category: form.category,
          features: form.features,
          targetAudience: form.targetAudience,
          priceRange: form.priceRange,
          marketplace: form.marketplaces[0],
          language: form.language
        });
      }
      setResult(listing);
      toast.success('Listing generated!');
    } catch (err) {
      toast.error(err.response?.data?.error || 'Generation failed');
    }
  };

  const handleImageResult = (data) => {
    setResult(data.listing);
  };

  const handleRegenerate = () => {
    if (lastForm) handleGenerate(lastForm);
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold mb-1">Generate Listing</h1>
        <p className="text-dark-400 text-sm">Create AI-powered product listings</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          <div className="flex gap-2 mb-4">
            <button
              onClick={() => setTab('form')}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition ${
                tab === 'form' ? 'bg-primary text-white' : 'bg-dark-800 text-dark-300'
              }`}
            >
              <Wand2 size={16} /> Product Details
            </button>
            {['pro', 'agency'].includes(user?.plan) && (
              <button
                onClick={() => setTab('image')}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition ${
                  tab === 'image' ? 'bg-primary text-white' : 'bg-dark-800 text-dark-300'
                }`}
              >
                <ImageIcon size={16} /> Image Upload
              </button>
            )}
          </div>

          <div className="card">
            {tab === 'form' ? (
              <SingleListingForm onSubmit={handleGenerate} loading={loading} />
            ) : (
              <ImageToListing onResult={handleImageResult} />
            )}
          </div>
        </div>

        <div>
          <h3 className="text-lg font-semibold mb-4">Generated Listing</h3>
          {result ? (
            <ListingOutput listing={result} onRegenerate={handleRegenerate} />
          ) : (
            <div className="card flex items-center justify-center min-h-[500px]">
              <p className="text-dark-500 text-center">
                Your AI-generated listing will appear here.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
