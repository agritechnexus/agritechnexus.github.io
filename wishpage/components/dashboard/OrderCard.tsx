"use client";

import { useState, useCallback } from "react";
import { Copy, Check, ExternalLink, Download } from "lucide-react";

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

interface OrderCardProps {
  order: Order;
}

const planBadgeStyles: Record<string, string> = {
  basic: "bg-gray-100 text-text-muted",
  premium: "bg-gold/10 text-gold",
  deluxe: "bg-gradient-to-r from-gold to-gold-light text-white",
};

export default function OrderCard({ order }: OrderCardProps) {
  const [copied, setCopied] = useState(false);

  const handleCopyLink = useCallback(() => {
    navigator.clipboard.writeText(`https://${order.siteUrl}`);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  }, [order.siteUrl]);

  return (
    <div className="bg-white rounded-[20px] border border-gray-100 shadow-card p-6 hover:shadow-card-hover transition-all duration-300">
      {/* Top row */}
      <div className="flex justify-between items-start">
        <span className="bg-emerald/10 text-emerald rounded-full px-3 py-1 text-xs font-semibold">
          {order.occasionEmoji} {order.occasion}
        </span>
        <span
          className={`rounded-full px-3 py-1 text-xs font-semibold uppercase ${
            planBadgeStyles[order.plan]
          }`}
        >
          {order.plan}
        </span>
      </div>

      {/* Recipient name */}
      <h3 className="font-heading text-lg font-bold text-text mt-3">
        {order.recipientName}
      </h3>

      {/* Site URL */}
      <a
        href={`https://${order.siteUrl}`}
        target="_blank"
        rel="noopener noreferrer"
        className="text-emerald text-sm font-mono mt-1 truncate block hover:underline"
      >
        {order.siteUrl}
      </a>

      {/* Stats row */}
      <div className="flex items-center gap-4 mt-3 text-sm text-text-muted">
        <span>👁 {order.views} views</span>
        <span>Created {order.createdAt}</span>
      </div>

      {/* Expiry */}
      <div className="mt-2 text-sm">
        {order.expired ? (
          <span className="text-red-500 font-medium">⚠️ Expired</span>
        ) : (
          <span className="text-text-muted">
            Expires in {order.expiresIn} days
          </span>
        )}
      </div>

      {/* Action buttons */}
      <div className="flex gap-2 mt-4">
        <a
          href={`https://${order.siteUrl}`}
          target="_blank"
          rel="noopener noreferrer"
          className="bg-emerald text-white rounded-full px-4 py-2 text-sm font-semibold hover:bg-emerald-light transition inline-flex items-center gap-1.5"
        >
          <ExternalLink className="w-3.5 h-3.5" />
          View Site
        </a>
        <button
          onClick={handleCopyLink}
          className="border border-emerald text-emerald rounded-full px-4 py-2 text-sm font-semibold hover:bg-emerald hover:text-white transition inline-flex items-center gap-1.5"
        >
          {copied ? (
            <>
              <Check className="w-3.5 h-3.5" />
              Copied!
            </>
          ) : (
            <>
              <Copy className="w-3.5 h-3.5" />
              Copy Link
            </>
          )}
        </button>
        <button className="text-text-muted hover:text-emerald text-sm font-semibold px-3 py-2 transition inline-flex items-center gap-1.5">
          <Download className="w-3.5 h-3.5" />
          Download QR
        </button>
      </div>
    </div>
  );
}
