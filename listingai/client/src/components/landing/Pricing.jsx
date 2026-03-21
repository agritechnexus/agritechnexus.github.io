import { Check } from 'lucide-react';
import { Link } from 'react-router-dom';

const plans = [
  {
    name: 'Free', price: '0', period: 'forever',
    features: ['5 listings/month', 'Single marketplace', 'English only', 'Standard AI model'],
    cta: 'Get Started', highlight: false,
  },
  {
    name: 'Starter', price: '499', period: '/month',
    features: ['100 listings/month', 'All 5 marketplaces', 'All 7 languages', 'Email support', 'Listing history'],
    cta: 'Start Free Trial', highlight: false,
  },
  {
    name: 'Pro', price: '1,499', period: '/month', badge: 'Most Popular',
    features: ['500 listings/month', 'All 5 marketplaces', 'All 7 languages', 'Bulk CSV upload', 'Image-to-listing', 'Premium AI model (Opus)', 'Priority support', 'Export to CSV'],
    cta: 'Go Pro', highlight: true,
  },
  {
    name: 'Agency', price: '4,999', period: '/month',
    features: ['Unlimited listings', 'All 5 marketplaces', 'All 7 languages', 'Bulk CSV upload', 'Image-to-listing', 'Premium AI model (Opus)', 'Dedicated support', 'API access', 'White-label option'],
    cta: 'Contact Sales', highlight: false,
  },
];

export default function Pricing() {
  return (
    <section id="pricing" className="py-24">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold mb-4">Simple, Transparent Pricing</h2>
          <p className="text-dark-400">Start free. Scale as you grow. No hidden fees.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {plans.map((plan) => (
            <div key={plan.name} className={`card relative flex flex-col ${plan.highlight ? 'border-primary ring-1 ring-primary/20 scale-105' : ''}`}>
              {plan.badge && (
                <span className="absolute -top-3 left-1/2 -translate-x-1/2 bg-primary text-white text-xs font-bold px-3 py-1 rounded-full">
                  {plan.badge}
                </span>
              )}
              <h3 className="text-lg font-bold mb-1">{plan.name}</h3>
              <div className="mb-6">
                <span className="text-4xl font-black">&#8377;{plan.price}</span>
                <span className="text-dark-400 text-sm">{plan.period}</span>
              </div>
              <ul className="space-y-3 mb-8 flex-1">
                {plan.features.map((f) => (
                  <li key={f} className="flex items-start gap-2 text-sm text-dark-200">
                    <Check size={16} className="text-primary mt-0.5 shrink-0" />
                    {f}
                  </li>
                ))}
              </ul>
              <Link
                to="/signup"
                className={`w-full text-center py-3 px-4 rounded-lg font-semibold transition-all ${
                  plan.highlight ? 'bg-primary hover:bg-primary-dark text-white' : 'border border-dark-500 hover:border-primary text-white'
                }`}
              >
                {plan.cta}
              </Link>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
