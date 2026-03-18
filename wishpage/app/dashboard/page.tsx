"use client";

import { useState, FormEvent } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import { Search, Plus } from "lucide-react";
import OrderCard from "@/components/dashboard/OrderCard";

interface Order {
  id: string;
  occasion: string;
  occasionEmoji: string;
  recipientName: string;
  plan: "basic" | "premium" | "deluxe";
  siteUrl: string;
  views: number;
  createdAt: string;
  expiresIn: number;
  expired: boolean;
}

const mockOrders: Order[] = [
  {
    id: "WP-1001",
    occasion: "Birthday",
    occasionEmoji: "🎂",
    recipientName: "Ravi Kumar",
    plan: "premium",
    siteUrl: "wishpage.in/s/ravi-birthday-x7k2",
    views: 124,
    createdAt: "Mar 15, 2026",
    expiresIn: 45,
    expired: false,
  },
  {
    id: "WP-1002",
    occasion: "Wedding",
    occasionEmoji: "💒",
    recipientName: "Priya & Arjun",
    plan: "deluxe",
    siteUrl: "wishpage.in/s/priya-arjun-wedding",
    views: 567,
    createdAt: "Mar 10, 2026",
    expiresIn: 340,
    expired: false,
  },
  {
    id: "WP-1003",
    occasion: "Anniversary",
    occasionEmoji: "🎊",
    recipientName: "Mom & Dad",
    plan: "premium",
    siteUrl: "wishpage.in/s/anniversary-25",
    views: 89,
    createdAt: "Feb 28, 2026",
    expiresIn: 60,
    expired: false,
  },
  {
    id: "WP-1004",
    occasion: "Birthday",
    occasionEmoji: "🎂",
    recipientName: "Sara Ahmed",
    plan: "basic",
    siteUrl: "wishpage.in/s/sara-bday",
    views: 45,
    createdAt: "Feb 1, 2026",
    expiresIn: 0,
    expired: true,
  },
  {
    id: "WP-1005",
    occasion: "Diwali",
    occasionEmoji: "🪔",
    recipientName: "The Sharma Family",
    plan: "premium",
    siteUrl: "wishpage.in/s/sharma-diwali",
    views: 234,
    createdAt: "Oct 20, 2025",
    expiresIn: 0,
    expired: true,
  },
  {
    id: "WP-1006",
    occasion: "Baby Shower",
    occasionEmoji: "👶",
    recipientName: "Neha & Rahul",
    plan: "deluxe",
    siteUrl: "wishpage.in/s/baby-shower-neha",
    views: 178,
    createdAt: "Mar 12, 2026",
    expiresIn: 350,
    expired: false,
  },
];

export default function DashboardPage() {
  const [email, setEmail] = useState("");
  const [searched, setSearched] = useState(false);

  const isEmptyResult = email.toLowerCase().includes("empty");
  const orders = isEmptyResult ? [] : mockOrders;

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (email.trim()) {
      setSearched(true);
    }
  };

  return (
    <div className="min-h-screen bg-cream">
      {/* Header bar */}
      <header className="bg-white border-b border-gray-100 px-6 py-4 flex items-center justify-between">
        <Link href="/" className="font-heading text-emerald font-bold text-xl">
          WishPage
        </Link>
        <span className="text-lg font-semibold">Dashboard</span>
        <Link
          href="/order/birthday"
          className="bg-emerald text-white rounded-full px-5 py-2.5 text-sm font-semibold hover:bg-emerald-light transition inline-flex items-center gap-1.5"
        >
          <Plus className="w-4 h-4" />
          New Order
        </Link>
      </header>

      {/* Main content */}
      <div className="max-w-4xl mx-auto px-4 py-12">
        {/* Email lookup section */}
        <div className="text-center mb-12">
          <h1 className="font-heading text-2xl font-bold">Find Your Orders</h1>
          <p className="text-text-muted mt-2">
            Enter your email to see your orders
          </p>
          <form
            onSubmit={handleSubmit}
            className="flex gap-3 max-w-md mx-auto mt-6"
          >
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="your@email.com"
              required
              className="flex-1 border border-gray-200 rounded-xl px-4 py-3 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-emerald/20 focus:border-emerald transition"
            />
            <button
              type="submit"
              className="bg-emerald text-white rounded-xl px-6 py-3 font-semibold hover:bg-emerald-light transition text-sm whitespace-nowrap"
            >
              Find My Orders
            </button>
          </form>
        </div>

        {/* Orders grid */}
        {searched && orders.length > 0 && (
          <div>
            <h2 className="text-lg font-semibold mb-4">Your Orders</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {orders.map((order, index) => (
                <motion.div
                  key={order.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1, duration: 0.4 }}
                >
                  <OrderCard order={order} />
                </motion.div>
              ))}
            </div>
          </div>
        )}

        {/* Empty state */}
        {searched && orders.length === 0 && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4 }}
            className="text-center py-16"
          >
            <Search className="w-16 h-16 text-gray-300 mx-auto" />
            <p className="text-lg font-semibold mt-4">
              No orders found for this email
            </p>
            <Link
              href="/order/birthday"
              className="inline-block mt-4 bg-emerald text-white rounded-full px-6 py-3 font-semibold hover:bg-emerald-light transition"
            >
              Create Your First WishPage →
            </Link>
          </motion.div>
        )}
      </div>
    </div>
  );
}
