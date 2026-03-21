import { useState } from 'react';
import Hero from '../components/landing/Hero';
import Features from '../components/landing/Features';
import HowItWorks from '../components/landing/HowItWorks';
import Pricing from '../components/landing/Pricing';
import Testimonials from '../components/landing/Testimonials';
import CTA from '../components/landing/CTA';
import SingleListingForm from '../components/generator/SingleListingForm';
import ListingOutput from '../components/generator/ListingOutput';
import { useListings } from '../hooks/useListings';
import { useAuth } from '../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

function FAQ() {
  const [open, setOpen] = useState(null);
  const faqs = [
    { q: 'What is ListingAI?', a: 'ListingAI is an AI-powered tool that generates SEO-optimized product listings for Indian e-commerce marketplaces like Amazon IN, Flipkart, Meesho, Shopify, and JioMart.' },
    { q: 'How many listings can I generate for free?', a: 'The free plan includes 5 listings per month, forever. No credit card required.' },
    { q: 'Which languages are supported?', a: 'We support English, Hindi, Telugu, Tamil, Kannada, Bengali, and Marathi.' },
    { q: 'How does the SEO score work?', a: 'Our AI analyzes keyword density, bullet point coverage, description quality, and marketplace rule compliance to give a 0-100 score with improvement tips.' },
    { q: 'Can I upload products in bulk?', a: 'Yes! Pro and Agency plan users can upload a CSV file with up to 500 products and generate all listings at once.' },
    { q: 'Is my data secure?', a: 'Absolutely. We use industry-standard encryption and never share your product data with third parties.' },
  ];

  return (
    <section className="py-24">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 className="text-3xl font-bold text-center mb-12">Frequently Asked Questions</h2>
        <div className="space-y-3">
          {faqs.map(({ q, a }, i) => (
            <div key={i} className="card cursor-pointer" onClick={() => setOpen(open === i ? null : i)}>
              <div className="flex items-center justify-between">
                <h3 className="font-medium text-sm">{q}</h3>
                <span className="text-dark-400 text-xl">{open === i ? '-' : '+'}</span>
              </div>
              {open === i && <p className="text-dark-400 text-sm mt-3">{a}</p>}
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default function LandingPage() {
  const { generateDemo, loading } = useListings();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [result, setResult] = useState(null);

  const handleDemo = async (form) => {
    // Check localStorage demo count
    const demoCount = parseInt(localStorage.getItem('listingai_demo_count') || '0');
    if (!user && demoCount >= 3) {
      toast.error('Free demo limit reached. Sign up to continue!');
      navigate('/signup');
      return;
    }

    try {
      // For demo, generate for first marketplace only
      const listing = await generateDemo({
        productName: form.productName,
        category: form.category,
        features: form.features,
        marketplace: form.marketplaces[0] || 'Amazon IN',
        language: form.language
      });
      setResult(listing);
      if (!user) {
        localStorage.setItem('listingai_demo_count', String(demoCount + 1));
      }
    } catch {
      toast.error('Generation failed. Please try again.');
    }
  };

  return (
    <div>
      <Hero />

      {/* Social proof bar */}
      <div className="border-y border-dark-800 py-6">
        <div className="max-w-7xl mx-auto px-4 flex items-center justify-center gap-8 flex-wrap">
          <span className="text-dark-500 text-sm">Optimized for:</span>
          {['Amazon IN', 'Flipkart', 'Meesho', 'Shopify', 'JioMart'].map(mp => (
            <span key={mp} className="text-dark-300 font-medium text-sm">{mp}</span>
          ))}
        </div>
      </div>

      <HowItWorks />
      <Features />

      {/* Live Demo */}
      <section id="demo" className="py-24 bg-dark-900/50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl sm:text-4xl font-bold mb-4">Try It Now — Free</h2>
            <p className="text-dark-400">No signup required for your first 3 listings</p>
          </div>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div className="card">
              <h3 className="text-lg font-semibold mb-6">Product Details</h3>
              <SingleListingForm onSubmit={handleDemo} loading={loading} />
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-6">Generated Listing</h3>
              {result ? (
                <ListingOutput listing={result} />
              ) : (
                <div className="card h-full flex items-center justify-center min-h-[400px]">
                  <p className="text-dark-500 text-center">
                    Your AI-generated listing will appear here.<br />
                    Fill in the form and click "Generate Listing".
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </section>

      <Pricing />
      <Testimonials />
      <FAQ />
      <CTA />
    </div>
  );
}
