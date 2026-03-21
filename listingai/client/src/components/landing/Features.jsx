import { Globe, Search, Languages, FileSpreadsheet, ImageIcon, BarChart3 } from 'lucide-react';

const features = [
  { icon: Globe, title: 'Multi-Marketplace', desc: 'One input generates listings formatted for Amazon IN, Flipkart, Meesho, Shopify & JioMart.' },
  { icon: Search, title: 'SEO Optimized', desc: 'Built-in keyword research ensures your listings rank higher in marketplace search results.' },
  { icon: Languages, title: 'Multi-Language', desc: 'Generate listings in English, Hindi, Telugu, Tamil, Kannada, Bengali & Marathi.' },
  { icon: FileSpreadsheet, title: 'Bulk CSV Upload', desc: 'Upload a CSV with 500 products and get all listings generated automatically.' },
  { icon: ImageIcon, title: 'Image-to-Listing', desc: 'Upload a product photo and AI automatically describes it and generates the listing.' },
  { icon: BarChart3, title: 'Smart SEO Score', desc: 'Every listing gets a 0-100 SEO score with actionable tips to improve ranking.' },
];

export default function Features() {
  return (
    <section id="features" className="py-24 relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold mb-4">Everything You Need to Sell More</h2>
          <p className="text-dark-400 max-w-xl mx-auto">Professional product listings in seconds, not hours. Built specifically for Indian marketplace sellers.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map(({ icon: Icon, title, desc }) => (
            <div key={title} className="card group hover:border-primary/30 transition-all duration-300">
              <div className="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center mb-4 group-hover:bg-primary/20 transition">
                <Icon size={24} className="text-primary" />
              </div>
              <h3 className="text-lg font-semibold mb-2">{title}</h3>
              <p className="text-dark-400 text-sm leading-relaxed">{desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
