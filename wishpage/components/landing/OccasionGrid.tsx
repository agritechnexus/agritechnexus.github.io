"use client";

import { motion } from "framer-motion";
import Link from "next/link";

const occasions = [
  { emoji: "🎂", label: "Birthdays", slug: "birthday", isGold: false },
  { emoji: "💍", label: "Engagements", slug: "wedding", isGold: true },
  { emoji: "💒", label: "Wedding Invites", slug: "wedding", isGold: true },
  { emoji: "🎊", label: "Anniversaries", slug: "anniversary", isGold: false },
  { emoji: "👶", label: "Baby Showers", slug: "baby-shower", isGold: false },
  { emoji: "🪔", label: "Diwali Greetings", slug: "festival", isGold: false },
  { emoji: "🌙", label: "Eid Mubarak", slug: "festival", isGold: false },
  { emoji: "🎄", label: "Christmas", slug: "festival", isGold: false },
  { emoji: "🎓", label: "Graduations", slug: "custom", isGold: false },
  { emoji: "🏢", label: "Corporate Events", slug: "corporate", isGold: false },
  { emoji: "👋", label: "Farewell Wishes", slug: "custom", isGold: false },
  { emoji: "🎉", label: "New Year", slug: "festival", isGold: true },
  { emoji: "💐", label: "Thank You Pages", slug: "custom", isGold: false },
  { emoji: "🏆", label: "Achievements", slug: "custom", isGold: false },
  { emoji: "📸", label: "Photo Stories", slug: "custom", isGold: false },
];

export default function OccasionGrid() {
  return (
    <section
      className="py-24 px-6"
      style={{
        background: "linear-gradient(to bottom, #FDF8F0, #EDF5F0)",
      }}
    >
      <div className="max-w-4xl mx-auto text-center">
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
        >
          <p className="text-xs uppercase tracking-[0.15em] text-[#0D6E4F] font-semibold mb-4">
            PERFECT FOR
          </p>
          <h2 className="font-heading text-3xl md:text-4xl font-bold mb-12 text-[#1C1C1C]">
            Every celebration deserves its own page.
          </h2>
        </motion.div>

        <motion.div
          className="flex flex-wrap justify-center gap-3"
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.2 }}
          viewport={{ once: true }}
        >
          {occasions.map((tag, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: i * 0.03 }}
              viewport={{ once: true }}
            >
              <Link
                href={`/order/${tag.slug}`}
                className={`inline-flex items-center gap-2 bg-white border rounded-full px-5 py-2.5 text-[0.95rem] font-medium transition-all duration-300 hover:scale-105 ${
                  tag.isGold
                    ? "border-[rgba(13,110,79,0.15)] hover:bg-[#D4A853] hover:text-white hover:border-[#D4A853]"
                    : "border-[rgba(13,110,79,0.15)] hover:bg-[#0D6E4F] hover:text-white hover:border-[#0D6E4F]"
                }`}
              >
                <span>{tag.emoji}</span>
                <span>{tag.label}</span>
              </Link>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
}
