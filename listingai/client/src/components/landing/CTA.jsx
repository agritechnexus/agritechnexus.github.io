import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';

export default function CTA() {
  return (
    <section className="py-24">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div className="card bg-gradient-to-br from-primary/10 via-dark-800 to-dark-800 border-primary/20 py-16 px-8">
          <h2 className="text-3xl sm:text-4xl font-bold mb-4">
            Stop Writing Boring Listings.
            <br />
            <span className="text-primary">Start Selling More.</span>
          </h2>
          <p className="text-dark-300 mb-8 max-w-lg mx-auto">
            Join 10,000+ Indian sellers who use ListingAI to create professional, high-converting product listings in seconds.
          </p>
          <Link to="/signup" className="btn-primary text-lg py-4 px-8 gap-2">
            Get Started Free <ArrowRight size={20} />
          </Link>
          <p className="text-dark-500 text-sm mt-4">No credit card required. 5 free listings every month.</p>
        </div>
      </div>
    </section>
  );
}
