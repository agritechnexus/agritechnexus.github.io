import { Zap } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="border-t border-dark-800 bg-dark-950">
      <div className="max-w-7xl mx-auto px-4 py-12 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
                <Zap size={18} className="text-white" />
              </div>
              <span className="text-lg font-bold">ListingAI</span>
            </div>
            <p className="text-dark-400 text-sm">AI-powered product listing generator for Indian e-commerce sellers.</p>
          </div>
          <div>
            <h4 className="font-semibold mb-3 text-sm">Product</h4>
            <ul className="space-y-2 text-sm text-dark-400">
              <li><a href="/#features" className="hover:text-white transition">Features</a></li>
              <li><a href="/#pricing" className="hover:text-white transition">Pricing</a></li>
              <li><a href="/#how-it-works" className="hover:text-white transition">How It Works</a></li>
            </ul>
          </div>
          <div>
            <h4 className="font-semibold mb-3 text-sm">Marketplaces</h4>
            <ul className="space-y-2 text-sm text-dark-400">
              <li>Amazon IN</li>
              <li>Flipkart</li>
              <li>Meesho</li>
              <li>Shopify</li>
              <li>JioMart</li>
            </ul>
          </div>
          <div>
            <h4 className="font-semibold mb-3 text-sm">Support</h4>
            <ul className="space-y-2 text-sm text-dark-400">
              <li><a href="#" className="hover:text-white transition">Help Center</a></li>
              <li><a href="#" className="hover:text-white transition">Contact Us</a></li>
              <li><a href="#" className="hover:text-white transition">Privacy Policy</a></li>
              <li><a href="#" className="hover:text-white transition">Terms of Service</a></li>
            </ul>
          </div>
        </div>
        <div className="mt-8 pt-8 border-t border-dark-800 text-center text-sm text-dark-500">
          &copy; {new Date().getFullYear()} ListingAI. All rights reserved.
        </div>
      </div>
    </footer>
  );
}
