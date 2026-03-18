"use client";

import { motion } from "framer-motion";

const steps = [
  {
    number: "1",
    title: "Tell Us Your Occasion",
    description:
      "Fill a quick form with the occasion type, names, your message, and upload photos.",
  },
  {
    number: "2",
    title: "We Design Your Site",
    description:
      "Our AI crafts a beautiful, animated website tailored to your celebration in seconds.",
  },
  {
    number: "3",
    title: "Review & Share",
    description:
      "Get your unique URL instantly. Preview it, then share with everyone you love.",
  },
  {
    number: "4",
    title: "They Open & Smile",
    description:
      "Recipients open a stunning, interactive website that makes the moment unforgettable.",
  },
];

export default function HowItWorks() {
  return (
    <section id="how-it-works" className="py-24 px-6" style={{ backgroundColor: "#FDF8F0" }}>
      <div className="max-w-6xl mx-auto text-center">
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
        >
          <p className="text-xs uppercase tracking-[0.15em] text-[#0D6E4F] font-semibold mb-4">
            HOW IT WORKS
          </p>
          <h2 className="font-heading text-3xl md:text-4xl font-bold text-[#1C1C1C] mb-4">
            From idea to live link, in four simple steps.
          </h2>
          <p className="text-[#6B7280] text-base max-w-lg mx-auto mb-16">
            No technical skills needed. Just tell us the occasion and we handle
            everything.
          </p>
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {steps.map((step, i) => (
            <motion.div
              key={step.number}
              className="bg-white rounded-[20px] border border-gray-100 shadow-card p-8 text-left hover:shadow-card-hover transition-all duration-300"
              style={{ cursor: "default" }}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: i * 0.1 }}
              viewport={{ once: true }}
              whileHover={{ y: -4 }}
            >
              <div className="w-11 h-11 rounded-[14px] bg-gradient-to-br from-[#0D6E4F] to-[#2A8F6A] flex items-center justify-center text-white font-bold text-sm mb-5">
                {step.number}
              </div>
              <h3 className="font-body font-bold text-[1.1rem] text-[#1C1C1C] mb-3">
                {step.title}
              </h3>
              <p className="text-[#6B7280] text-[0.92rem] leading-relaxed">
                {step.description}
              </p>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
