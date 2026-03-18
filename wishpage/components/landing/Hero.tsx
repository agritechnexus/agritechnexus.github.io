"use client";

import { motion } from "framer-motion";
import Link from "next/link";
import { ArrowRight, ChevronDown } from "lucide-react";

const particles = [
  { left: "10%", top: "20%", color: "#34D399", delay: "0s" },
  { left: "25%", top: "60%", color: "#D4A853", delay: "1s" },
  { left: "40%", top: "15%", color: "#34D399", delay: "2s" },
  { left: "55%", top: "75%", color: "#D4A853", delay: "3s" },
  { left: "70%", top: "30%", color: "#34D399", delay: "4s" },
  { left: "80%", top: "55%", color: "#D4A853", delay: "5s" },
  { left: "15%", top: "80%", color: "#34D399", delay: "6s" },
  { left: "90%", top: "40%", color: "#D4A853", delay: "7s" },
];

export default function Hero() {
  return (
    <section
      className="relative overflow-hidden min-h-screen flex items-center justify-center"
      style={{
        background: "linear-gradient(to bottom, #0F1A14, #0A2618, #0F3325)",
      }}
    >
      {/* Aurora blobs */}
      <div
        className="aurora-blob absolute w-[600px] h-[600px] top-1/4 left-1/4 -translate-x-1/2 -translate-y-1/2"
        style={{
          background:
            "radial-gradient(circle, rgba(52,211,153,0.15), transparent 70%)",
        }}
      />
      <div
        className="aurora-blob absolute w-[600px] h-[600px] bottom-1/3 right-1/4 translate-x-1/2 translate-y-1/2"
        style={{
          background:
            "radial-gradient(circle, rgba(212,168,83,0.1), transparent 70%)",
          animationDelay: "-7s",
        }}
      />

      {/* Floating particles */}
      {particles.map((p, i) => (
        <div
          key={i}
          className="float-particle absolute w-1 h-1 rounded-full"
          style={{
            left: p.left,
            top: p.top,
            backgroundColor: p.color,
            animationDelay: p.delay,
          }}
        />
      ))}

      {/* Content */}
      <div className="relative z-10 text-center max-w-3xl mx-auto px-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0, duration: 0.6 }}
        >
          <span className="inline-flex items-center bg-[rgba(52,211,153,0.1)] border border-[rgba(52,211,153,0.2)] rounded-full px-5 py-2 text-xs uppercase tracking-[0.08em] text-[#34D399]">
            🎉 Now Serving All of India
          </span>
        </motion.div>

        <motion.h1
          className="font-heading text-white font-bold leading-tight mt-6"
          style={{ fontSize: "clamp(2.5rem, 8vw, 5rem)" }}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.15, duration: 0.6 }}
        >
          Turn Any Occasion Into a{" "}
          <span className="gradient-text">Beautiful Website</span>
        </motion.h1>

        <motion.p
          className="font-body text-white/65 text-lg max-w-[580px] mx-auto mt-6"
          style={{ fontSize: "1.2rem" }}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3, duration: 0.6 }}
        >
          Birthday wishes, wedding invitations, festival greetings — delivered
          as a stunning, shareable website link in under 2 hours.
        </motion.p>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.45, duration: 0.6 }}
          className="mt-8"
        >
          <Link
            href="#pricing"
            className="inline-flex items-center gap-2 bg-gradient-to-r from-[#0D6E4F] to-[#2A8F6A] text-white rounded-full px-8 py-4 font-semibold shadow-[0_0_30px_rgba(13,110,79,0.4)] hover:translate-y-[-2px] hover:shadow-[0_0_40px_rgba(13,110,79,0.6)] transition-all duration-300"
            style={{ borderRadius: "100px" }}
          >
            See Pricing
            <ArrowRight className="w-5 h-5" />
          </Link>
        </motion.div>
      </div>

      {/* Scroll indicator */}
      <div className="absolute bottom-8 left-1/2 -translate-x-1/2 text-center">
        <span className="gentle-bounce block text-white/40 text-sm">
          Scroll to explore
        </span>
        <ChevronDown className="w-5 h-5 text-white/40 mx-auto mt-1" />
      </div>
    </section>
  );
}
