"use client";

import { motion } from "framer-motion";
import Link from "next/link";
import { Check, X } from "lucide-react";

interface Feature {
  text: string;
  enabled: boolean;
}

interface PricingPlan {
  plan: string;
  price: string;
  note: string;
  features: Feature[];
  featured: boolean;
  priceColor: string;
  buttonStyle: "outline-emerald" | "filled-emerald" | "outline-gold";
}

const plans: PricingPlan[] = [
  {
    plan: "BASIC",
    price: "₹199",
    note: "One-time · Delivered in 2 hours",
    featured: false,
    priceColor: "#1C1C1C",
    buttonStyle: "outline-emerald",
    features: [
      { text: "Custom message & greeting", enabled: true },
      { text: "Up to 5 photos", enabled: true },
      { text: "Beautiful responsive design", enabled: true },
      { text: "Unique shareable link", enabled: true },
      { text: "30-day hosting included", enabled: true },
      { text: "Background music", enabled: false },
      { text: "Animations & effects", enabled: false },
      { text: "RSVP / Guestbook", enabled: false },
    ],
  },
  {
    plan: "PREMIUM",
    price: "₹499",
    note: "One-time · Delivered in 2 hours",
    featured: true,
    priceColor: "",
    buttonStyle: "filled-emerald",
    features: [
      { text: "Everything in Basic", enabled: true },
      { text: "Up to 15 photos", enabled: true },
      { text: "Smooth animations & effects", enabled: true },
      { text: "Background music of your choice", enabled: true },
      { text: "Countdown timer", enabled: true },
      { text: "Photo gallery carousel", enabled: true },
      { text: "Confetti / fireworks effects", enabled: true },
      { text: "90-day hosting included", enabled: true },
    ],
  },
  {
    plan: "DELUXE",
    price: "₹999",
    note: "One-time · Delivered in 4 hours",
    featured: false,
    priceColor: "#D4A853",
    buttonStyle: "outline-gold",
    features: [
      { text: "Everything in Premium", enabled: true },
      { text: "Unlimited photos & video embed", enabled: true },
      { text: "RSVP / Guestbook feature", enabled: true },
      { text: "Custom domain support", enabled: true },
      { text: "QR code for sharing", enabled: true },
      { text: "Custom color theme", enabled: true },
      { text: "Priority support", enabled: true },
      { text: "1-year hosting included", enabled: true },
    ],
  },
];

function FeatureItem({
  feature,
  dark,
}: {
  feature: Feature;
  dark: boolean;
}) {
  if (feature.enabled) {
    return (
      <li className="flex items-center gap-3">
        <span
          className={`w-5 h-5 rounded-full flex items-center justify-center flex-shrink-0 ${
            dark ? "bg-[rgba(52,211,153,0.15)]" : "bg-[rgba(13,110,79,0.1)]"
          }`}
        >
          <Check
            className={`w-3 h-3 ${dark ? "text-[#34D399]" : "text-[#0D6E4F]"}`}
          />
        </span>
        <span className={dark ? "text-white/90" : ""}>{feature.text}</span>
      </li>
    );
  }

  return (
    <li className="flex items-center gap-3 text-[#6B7280]/50">
      <span className="w-5 h-5 rounded-full bg-gray-100 flex items-center justify-center flex-shrink-0">
        <X className="w-3 h-3 text-gray-400" />
      </span>
      <span>{feature.text}</span>
    </li>
  );
}

function ButtonLink({
  style,
}: {
  style: "outline-emerald" | "filled-emerald" | "outline-gold";
}) {
  const base =
    "block w-full py-3 rounded-full font-semibold text-center transition-all duration-300 mt-8";

  if (style === "outline-emerald") {
    return (
      <Link
        href="/order/birthday"
        className={`${base} border-2 border-[#0D6E4F] text-[#0D6E4F] hover:bg-[#0D6E4F] hover:text-white`}
      >
        Get Started
      </Link>
    );
  }

  if (style === "filled-emerald") {
    return (
      <Link
        href="/order/birthday"
        className={`${base} bg-gradient-to-r from-[#0D6E4F] to-[#2A8F6A] text-white shadow-[0_0_30px_rgba(13,110,79,0.4)] hover:shadow-[0_0_40px_rgba(13,110,79,0.6)]`}
      >
        Get Started
      </Link>
    );
  }

  return (
    <Link
      href="/order/birthday"
      className={`${base} border-2 border-[#D4A853] text-[#D4A853] hover:bg-[#D4A853] hover:text-white`}
    >
      Get Started
    </Link>
  );
}

export default function PricingCards() {
  return (
    <section id="pricing" className="py-24 px-6" style={{ backgroundColor: "#FDF8F0" }}>
      <div className="max-w-5xl mx-auto text-center">
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
        >
          <p className="text-xs uppercase tracking-[0.15em] text-[#0D6E4F] font-semibold mb-4">
            SIMPLE PRICING
          </p>
          <h2 className="font-heading text-3xl md:text-4xl font-bold text-[#1C1C1C] mb-4">
            Choose your plan.
          </h2>
          <p className="text-[#6B7280] text-base max-w-lg mx-auto">
            No hidden fees. Pay once, enjoy your site for the full duration.
          </p>
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-16 items-start">
          {plans.map((plan, i) => (
            <motion.div
              key={plan.plan}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: i * 0.1 }}
              viewport={{ once: true }}
              className={
                plan.featured
                  ? "bg-[#0F1A14] rounded-[24px] border border-[rgba(13,110,79,0.3)] p-8 relative overflow-hidden"
                  : "bg-white rounded-[24px] border border-gray-100 shadow-card p-8"
              }
            >
              {/* Featured top gradient line */}
              {plan.featured && (
                <div className="absolute top-0 left-0 right-0 h-[3px] bg-gradient-to-r from-[#34D399] to-[#D4A853]" />
              )}

              {/* Featured badge */}
              {plan.featured && (
                <span className="bg-[rgba(52,211,153,0.15)] text-[#34D399] text-xs font-semibold px-3 py-1 rounded-full inline-block mb-4">
                  ⭐ MOST POPULAR
                </span>
              )}

              <p
                className={`text-xs uppercase tracking-widest font-semibold ${
                  plan.featured ? "text-white/60" : "text-[#6B7280]"
                }`}
              >
                {plan.plan}
              </p>

              <p
                className={`font-heading text-[3.2rem] font-bold mt-2 ${
                  plan.featured ? "gradient-text" : ""
                }`}
                style={!plan.featured ? { color: plan.priceColor } : undefined}
              >
                {plan.price}
              </p>

              <p
                className={`text-sm mt-1 ${
                  plan.featured ? "text-white/60" : "text-[#6B7280]"
                }`}
              >
                {plan.note}
              </p>

              <div
                className={`border-t my-6 ${
                  plan.featured ? "border-white/10" : "border-gray-100"
                }`}
              />

              <ul className="space-y-3 text-left text-[0.92rem]">
                {plan.features.map((feature, fi) => (
                  <FeatureItem
                    key={fi}
                    feature={feature}
                    dark={plan.featured}
                  />
                ))}
              </ul>

              <ButtonLink style={plan.buttonStyle} />
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
