"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { Menu, X } from "lucide-react";
import { AnimatePresence, motion } from "framer-motion";

const navLinks = [
  { label: "How It Works", href: "#how-it-works" },
  { label: "Pricing", href: "#pricing" },
  { label: "Dashboard", href: "/dashboard" },
];

export default function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [drawerOpen, setDrawerOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 50);
    };
    window.addEventListener("scroll", handleScroll, { passive: true });
    handleScroll();
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  // Lock body scroll when drawer is open
  useEffect(() => {
    if (drawerOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
    return () => {
      document.body.style.overflow = "";
    };
  }, [drawerOpen]);

  const textColor = scrolled ? "text-[#0F1A14]" : "text-white";

  return (
    <>
      <nav
        className={`fixed top-0 left-0 z-50 w-full h-[72px] flex items-center px-[6%] transition-all duration-300 ${
          scrolled
            ? "bg-[#FDF8F0] shadow-[0_1px_8px_rgba(0,0,0,0.08)]"
            : "bg-transparent"
        }`}
      >
        {/* Logo */}
        <Link
          href="/"
          className="font-heading font-bold text-xl text-[#0D6E4F] shrink-0"
        >
          WishPage
        </Link>

        {/* Spacer */}
        <div className="flex-1" />

        {/* Desktop Nav */}
        <div className="hidden md:flex items-center gap-8">
          {navLinks.map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className={`text-sm font-body transition-colors duration-200 ${textColor} opacity-80 hover:text-[#0D6E4F] hover:opacity-100`}
            >
              {link.label}
            </Link>
          ))}

          <Link
            href="/order/birthday"
            className="inline-flex items-center justify-center bg-gradient-to-r from-[#0D6E4F] to-[#34D399] text-white text-sm font-semibold rounded-[100px] px-6 py-2.5 transition-all duration-200 hover:shadow-[0_4px_14px_rgba(13,110,79,0.35)] hover:-translate-y-[1px]"
          >
            Get Started
          </Link>
        </div>

        {/* Mobile Hamburger */}
        <button
          className="md:hidden p-2 -mr-2"
          onClick={() => setDrawerOpen(true)}
          aria-label="Open menu"
        >
          <Menu className={`w-6 h-6 ${scrolled ? "text-[#0F1A14]" : "text-white"}`} />
        </button>
      </nav>

      {/* Mobile Drawer */}
      <AnimatePresence>
        {drawerOpen && (
          <>
            {/* Overlay */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.2 }}
              className="fixed inset-0 z-50 bg-black/40"
              onClick={() => setDrawerOpen(false)}
            />

            {/* Drawer */}
            <motion.div
              initial={{ x: "100%" }}
              animate={{ x: 0 }}
              exit={{ x: "100%" }}
              transition={{ type: "tween", duration: 0.3, ease: "easeInOut" }}
              className="fixed top-0 right-0 z-50 h-full w-[280px] bg-white shadow-xl flex flex-col"
            >
              {/* Close Button */}
              <div className="flex items-center justify-end h-[72px] px-6">
                <button
                  onClick={() => setDrawerOpen(false)}
                  aria-label="Close menu"
                  className="p-2 -mr-2"
                >
                  <X className="w-6 h-6 text-[#0F1A14]" />
                </button>
              </div>

              {/* Nav Links */}
              <div className="flex flex-col gap-2 px-6 mt-4">
                {navLinks.map((link) => (
                  <Link
                    key={link.href}
                    href={link.href}
                    onClick={() => setDrawerOpen(false)}
                    className="text-[#0F1A14] text-base font-body py-3 border-b border-black/5 transition-colors duration-200 hover:text-[#0D6E4F]"
                  >
                    {link.label}
                  </Link>
                ))}
              </div>

              {/* CTA */}
              <div className="px-6 mt-8">
                <Link
                  href="/order/birthday"
                  onClick={() => setDrawerOpen(false)}
                  className="flex items-center justify-center w-full bg-gradient-to-r from-[#0D6E4F] to-[#34D399] text-white text-sm font-semibold rounded-[100px] px-6 py-3 transition-all duration-200 hover:shadow-[0_4px_14px_rgba(13,110,79,0.35)]"
                >
                  Get Started
                </Link>
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </>
  );
}
