import { ClipboardList, Cpu, Copy } from 'lucide-react';

const steps = [
  { icon: ClipboardList, step: '01', title: 'Enter Product Details', desc: 'Add your product name, category, and key features. Takes 30 seconds.' },
  { icon: Cpu, step: '02', title: 'AI Generates Listings', desc: 'Claude AI creates SEO-optimized titles, bullet points, descriptions & keywords.' },
  { icon: Copy, step: '03', title: 'Copy to Marketplace', desc: 'Copy the formatted listing directly to your marketplace seller dashboard.' },
];

export default function HowItWorks() {
  return (
    <section id="how-it-works" className="py-24 bg-dark-900/50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold mb-4">How It Works</h2>
          <p className="text-dark-400">Three simple steps to professional product listings</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {steps.map(({ icon: Icon, step, title, desc }, i) => (
            <div key={step} className="relative text-center">
              {i < 2 && <div className="hidden md:block absolute top-12 left-[60%] w-[80%] h-px bg-gradient-to-r from-primary/50 to-transparent" />}
              <div className="w-24 h-24 bg-dark-800 border border-dark-700 rounded-2xl flex items-center justify-center mx-auto mb-6 relative">
                <Icon size={36} className="text-primary" />
                <span className="absolute -top-2 -right-2 w-7 h-7 bg-primary rounded-full flex items-center justify-center text-xs font-bold">{step}</span>
              </div>
              <h3 className="text-lg font-semibold mb-2">{title}</h3>
              <p className="text-dark-400 text-sm">{desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
