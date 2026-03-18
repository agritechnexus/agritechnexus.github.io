"use client";

import { useState, useEffect, useCallback } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import { Copy, Check, QrCode, Mail, Share2 } from "lucide-react";
import ProgressTimeline from "@/components/status/ProgressTimeline";

type Status = "PAID" | "GENERATING" | "DEPLOYING" | "DELIVERED" | "FAILED";

const CONFETTI_COLORS = ["#34D399", "#D4A853", "#F0D48A", "#F472B6", "#60A5FA"];

function ConfettiPiece({ index }: { index: number }) {
  const left = Math.random() * 100;
  const color = CONFETTI_COLORS[index % CONFETTI_COLORS.length];
  const size = Math.random() > 0.5 ? "w-2 h-2" : "w-3 h-3";
  const delay = Math.random() * 2;
  const duration = 3 + Math.random() * 2;

  return (
    <div
      className={`absolute top-0 ${size} rounded-sm pointer-events-none`}
      style={{
        left: `${left}%`,
        backgroundColor: color,
        animation: `confetti-fall ${duration}s ease-in ${delay}s forwards`,
        opacity: 0,
        animationFillMode: "forwards",
      }}
    />
  );
}

export default function StatusPageClient() {
  const params = useParams();
  const orderId = params.orderId as string;

  const [status, setStatus] = useState<Status>("PAID");
  const [copied, setCopied] = useState(false);
  const [showConfetti, setShowConfetti] = useState(false);

  const siteUrl = "https://wishpage.in/s/ravi-birthday-x7k2";
  const displayUrl = "wishpage.in/s/ravi-birthday-x7k2";

  useEffect(() => {
    const timers: ReturnType<typeof setTimeout>[] = [];

    timers.push(
      setTimeout(() => setStatus("GENERATING"), 5000),
      setTimeout(() => setStatus("DEPLOYING"), 10000),
      setTimeout(() => {
        setStatus("DELIVERED");
        setShowConfetti(true);
      }, 15000)
    );

    return () => timers.forEach(clearTimeout);
  }, []);

  useEffect(() => {
    if (showConfetti) {
      const timer = setTimeout(() => setShowConfetti(false), 7000);
      return () => clearTimeout(timer);
    }
  }, [showConfetti]);

  const handleCopy = useCallback(() => {
    navigator.clipboard.writeText(siteUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  }, []);

  const shareText = encodeURIComponent(
    `Check out this WishPage! ${siteUrl}`
  );

  return (
    <div className="min-h-screen bg-cream flex flex-col items-center justify-start pt-12 px-4 relative overflow-hidden">
      {showConfetti && (
        <div className="fixed inset-0 pointer-events-none z-50">
          {Array.from({ length: 30 }).map((_, i) => (
            <ConfettiPiece key={i} index={i} />
          ))}
        </div>
      )}

      <Link href="/" className="mb-8">
        <span className="font-heading text-emerald text-xl font-bold">
          WishPage
        </span>
      </Link>

      <div className="bg-white rounded-[24px] shadow-card p-8 w-full max-w-[600px]">
        <h1 className="font-heading text-xl font-bold text-center mb-2">
          Order #{orderId}
        </h1>
        <p className="text-text-muted text-sm text-center mb-8">
          Usually takes about 30-60 seconds
        </p>

        <ProgressTimeline currentStatus={status} />

        <AnimatePresence>
          {status === "DELIVERED" && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5 }}
            >
              <div className="border-t border-gray-100 my-8" />

              <h2 className="font-heading text-2xl font-bold text-center gradient-text">
                🎉 Your WishPage is Live!
              </h2>

              <div className="bg-cream rounded-xl p-4 mt-6 flex items-center justify-between gap-3">
                <span className="text-emerald font-mono text-sm truncate">
                  {displayUrl}
                </span>
                <button
                  onClick={handleCopy}
                  className="p-2 hover:bg-emerald/10 rounded-lg transition shrink-0"
                >
                  {copied ? (
                    <Check className="w-5 h-5 text-emerald" />
                  ) : (
                    <Copy className="w-5 h-5 text-text-muted" />
                  )}
                </button>
              </div>

              <div className="mx-auto mt-6 w-[200px] h-[200px] bg-gray-100 rounded-xl flex flex-col items-center justify-center gap-2">
                <QrCode className="w-12 h-12 text-gray-400" />
                <span className="text-sm text-text-muted">QR Code</span>
              </div>

              <div className="flex gap-3 justify-center mt-6">
                <a
                  href={`https://wa.me/?text=${shareText}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="bg-[#25D366] text-white rounded-full px-5 py-2.5 text-sm font-semibold inline-flex items-center gap-2 hover:opacity-90 transition"
                >
                  <Share2 className="w-4 h-4" />
                  WhatsApp
                </a>
                <a
                  href={`https://twitter.com/intent/tweet?text=${shareText}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="bg-black text-white rounded-full px-5 py-2.5 text-sm font-semibold inline-flex items-center gap-2 hover:opacity-90 transition"
                >
                  Twitter/X
                </a>
                <a
                  href={`mailto:?subject=Check%20out%20this%20WishPage!&body=${shareText}`}
                  className="bg-gray-600 text-white rounded-full px-5 py-2.5 text-sm font-semibold inline-flex items-center gap-2 hover:opacity-90 transition"
                >
                  <Mail className="w-4 h-4" />
                  Email
                </a>
              </div>

              <Link
                href="#"
                className="mt-8 w-full bg-gradient-to-r from-emerald to-emerald-light text-white rounded-full py-4 font-semibold text-lg text-center block hover:opacity-90 transition"
              >
                View Your Site →
              </Link>
            </motion.div>
          )}
        </AnimatePresence>

        <AnimatePresence>
          {status === "FAILED" && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5 }}
              className="bg-red-50 border border-red-200 rounded-xl p-6 mt-6"
            >
              <h3 className="font-heading text-lg font-bold text-red-700">
                Something went wrong
              </h3>
              <p className="text-red-600 text-sm mt-1">
                We&apos;re looking into it. Please contact support.
              </p>
              <a
                href="mailto:support@wishpage.in"
                className="inline-block mt-3 text-red-700 font-semibold text-sm underline hover:text-red-800 transition"
              >
                Contact Support
              </a>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
}
