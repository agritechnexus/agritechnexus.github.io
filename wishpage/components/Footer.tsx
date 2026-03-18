"use client";

import Link from "next/link";

const quickLinks = [
  { label: "How It Works", href: "#how-it-works" },
  { label: "Pricing", href: "#pricing" },
  { label: "Dashboard", href: "/dashboard" },
  { label: "Contact", href: "mailto:hello@wishpage.in" },
];

const legalLinks = [
  { label: "Terms of Service", href: "#" },
  { label: "Privacy Policy", href: "#" },
  { label: "Refund Policy", href: "#" },
];

export default function Footer() {
  return (
    <footer className="bg-[#0F1A14] py-16">
      <div className="max-w-[1200px] mx-auto px-[6%]">
        {/* Columns */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-12 md:gap-8">
          {/* Brand Column */}
          <div>
            <h3 className="font-heading font-bold text-[1.25rem] text-[#0D6E4F]">
              WishPage
            </h3>
            <p className="mt-4 text-white/60 text-[0.9rem] font-body leading-relaxed max-w-[280px]">
              Turn any occasion into a beautiful, shareable website.
            </p>
          </div>

          {/* Quick Links Column */}
          <div>
            <h4 className="text-white text-[0.85rem] uppercase tracking-[0.1em] font-semibold font-body mb-5">
              Quick Links
            </h4>
            <ul className="flex flex-col gap-3">
              {quickLinks.map((link) => (
                <li key={link.label}>
                  <Link
                    href={link.href}
                    className="text-white/50 text-[0.9rem] font-body transition-colors duration-200 hover:text-white/80"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Legal Column */}
          <div>
            <h4 className="text-white text-[0.85rem] uppercase tracking-[0.1em] font-semibold font-body mb-5">
              Legal
            </h4>
            <ul className="flex flex-col gap-3">
              {legalLinks.map((link) => (
                <li key={link.label}>
                  <Link
                    href={link.href}
                    className="text-white/50 text-[0.9rem] font-body transition-colors duration-200 hover:text-white/80"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="mt-12 pt-6 border-t border-white/10 text-center">
          <p className="text-white/40 text-[0.85rem] font-body">
            &copy; 2026 WishPage &middot; Hyderabad, India &middot; Built with
            ❤️
          </p>
        </div>
      </div>
    </footer>
  );
}
