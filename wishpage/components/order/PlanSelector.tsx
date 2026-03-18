"use client";

import { motion } from "framer-motion";
import { Check } from "lucide-react";

interface PlanSelectorProps {
  selectedPlan: string;
  setSelectedPlan: (plan: string) => void;
}

const PLANS = [
  {
    id: "basic",
    name: "Basic",
    price: 199,
    features: ["Custom greeting", "Up to 5 photos", "30-day hosting"],
  },
  {
    id: "premium",
    name: "Premium",
    price: 499,
    features: [
      "Everything in Basic",
      "Animations & music",
      "90-day hosting",
    ],
  },
  {
    id: "deluxe",
    name: "Deluxe",
    price: 999,
    features: [
      "Everything in Premium",
      "RSVP & guestbook",
      "1-year hosting",
    ],
  },
];

export default function PlanSelector({
  selectedPlan,
  setSelectedPlan,
}: PlanSelectorProps) {
  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      {PLANS.map((plan) => {
        const isSelected = selectedPlan === plan.id;

        return (
          <motion.button
            key={plan.id}
            type="button"
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => setSelectedPlan(plan.id)}
            className={`border-2 rounded-2xl p-5 cursor-pointer transition-all text-left ${
              isSelected
                ? "border-emerald shadow-lg bg-emerald/5"
                : "border-gray-200 hover:border-emerald/30"
            }`}
          >
            {/* Radio indicator + plan name */}
            <div className="flex items-center gap-2 mb-3">
              <div
                className={`w-5 h-5 rounded-full border-2 flex items-center justify-center transition-colors ${
                  isSelected ? "border-emerald bg-emerald" : "border-gray-300"
                }`}
              >
                {isSelected && <Check size={12} className="text-white" strokeWidth={3} />}
              </div>
              <span className="text-xs font-bold uppercase tracking-wider text-text">
                {plan.name}
              </span>
            </div>

            {/* Price */}
            <div className="font-heading text-[2rem] text-text leading-tight mb-3">
              &#8377;{plan.price}
            </div>

            {/* Features */}
            <ul className="space-y-1.5">
              {plan.features.map((feat) => (
                <li key={feat} className="flex items-start gap-2 text-sm text-text-muted">
                  <Check size={14} className="text-emerald mt-0.5 flex-shrink-0" />
                  {feat}
                </li>
              ))}
            </ul>
          </motion.button>
        );
      })}
    </div>
  );
}
