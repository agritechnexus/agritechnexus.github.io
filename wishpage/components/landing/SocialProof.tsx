"use client";

import { motion } from "framer-motion";

export default function SocialProof() {
  return (
    <section
      className="py-20 px-6"
      style={{
        background: "linear-gradient(to bottom, #EDF5F0, #FDF8F0)",
      }}
    >
      <div className="max-w-3xl mx-auto text-center">
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
        >
          <span
            className="font-heading text-[6rem] leading-none block mb-4"
            style={{ color: "rgba(13,110,79,0.1)" }}
          >
            &ldquo;
          </span>

          <blockquote className="font-heading italic text-xl md:text-2xl text-[#1C1C1C] leading-relaxed">
            She opened the link and literally started crying happy tears. Way
            better than a WhatsApp forward.
          </blockquote>

          <p className="text-[#6B7280] text-sm mt-6">
            &mdash; Launch customer, Hyderabad
          </p>
        </motion.div>
      </div>
    </section>
  );
}
