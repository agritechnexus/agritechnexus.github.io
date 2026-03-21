import { Star } from 'lucide-react';

const testimonials = [
  {
    name: 'Priya Sharma',
    role: 'Amazon IN Seller',
    text: 'ListingAI saved me 4 hours daily. My product listings now rank on page 1 and my sales increased by 40% in the first month.',
    stars: 5,
  },
  {
    name: 'Rajesh Kumar',
    role: 'Flipkart & Meesho Seller',
    text: 'The multi-marketplace feature is a game-changer. I generate listings for 3 platforms at once. The Hindi language option helped me reach Tier 2 cities.',
    stars: 5,
  },
  {
    name: 'Anita Desai',
    role: 'Shopify Store Owner',
    text: 'The SEO scores and tips helped me understand what makes a good listing. My Shopify store traffic doubled after using ListingAI-generated descriptions.',
    stars: 5,
  },
];

export default function Testimonials() {
  return (
    <section className="py-24 bg-dark-900/50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold mb-4">Loved by Indian Sellers</h2>
          <p className="text-dark-400">See what our users say about ListingAI</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {testimonials.map((t) => (
            <div key={t.name} className="card">
              <div className="flex gap-1 mb-4">
                {Array.from({ length: t.stars }).map((_, i) => (
                  <Star key={i} size={16} className="text-yellow-400 fill-yellow-400" />
                ))}
              </div>
              <p className="text-dark-200 text-sm leading-relaxed mb-4">&ldquo;{t.text}&rdquo;</p>
              <div>
                <p className="font-semibold text-sm">{t.name}</p>
                <p className="text-dark-400 text-xs">{t.role}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
