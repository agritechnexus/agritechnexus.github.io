import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Sparkles } from 'lucide-react';

const TYPING_WORDS = ['Amazon IN', 'Flipkart', 'Meesho', 'Shopify', 'JioMart'];

export default function Hero() {
  const [wordIndex, setWordIndex] = useState(0);
  const [text, setText] = useState('');
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    const word = TYPING_WORDS[wordIndex];
    const timeout = setTimeout(() => {
      if (!deleting) {
        setText(word.slice(0, text.length + 1));
        if (text.length === word.length) {
          setTimeout(() => setDeleting(true), 1500);
        }
      } else {
        setText(word.slice(0, text.length - 1));
        if (text.length === 0) {
          setDeleting(false);
          setWordIndex((i) => (i + 1) % TYPING_WORDS.length);
        }
      }
    }, deleting ? 50 : 100);
    return () => clearTimeout(timeout);
  }, [text, deleting, wordIndex]);

  return (
    <section className="relative min-h-screen flex items-center pt-16 overflow-hidden">
      {/* Gradient mesh background */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute top-1/4 -left-1/4 w-96 h-96 bg-primary/20 rounded-full blur-3xl" />
        <div className="absolute bottom-1/4 -right-1/4 w-96 h-96 bg-orange-600/10 rounded-full blur-3xl" />
      </div>

      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div className="inline-flex items-center gap-2 bg-primary/10 border border-primary/20 rounded-full px-4 py-1.5 mb-8">
          <Sparkles size={14} className="text-primary" />
          <span className="text-sm text-primary font-medium">Powered by Claude AI</span>
        </div>

        <h1 className="text-4xl sm:text-5xl md:text-7xl font-black leading-tight mb-6">
          Turn Product Details Into
          <br />
          <span className="text-primary">Sales-Ready Listings</span>
          <br />
          in 30 Seconds
        </h1>

        <p className="text-lg sm:text-xl text-dark-300 max-w-2xl mx-auto mb-4">
          AI-powered listings for{' '}
          <span className="text-white font-semibold cursor-blink">{text}</span>
        </p>
        <p className="text-dark-400 mb-8">Used by 10,000+ Indian sellers to boost conversions</p>

        <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-16">
          <Link to="/dashboard/generate" className="btn-primary text-lg py-4 px-8 gap-2">
            Generate Free Listing <ArrowRight size={20} />
          </Link>
          <a href="#demo" className="btn-secondary text-lg py-4 px-8">
            Try Live Demo
          </a>
        </div>

        {/* Stats bar */}
        <div className="grid grid-cols-3 gap-8 max-w-lg mx-auto">
          <div>
            <p className="text-3xl font-bold text-primary">50K+</p>
            <p className="text-sm text-dark-400">Listings Generated</p>
          </div>
          <div>
            <p className="text-3xl font-bold text-primary">10K+</p>
            <p className="text-sm text-dark-400">Active Sellers</p>
          </div>
          <div>
            <p className="text-3xl font-bold text-primary">35%</p>
            <p className="text-sm text-dark-400">Avg. Sales Boost</p>
          </div>
        </div>
      </div>
    </section>
  );
}
