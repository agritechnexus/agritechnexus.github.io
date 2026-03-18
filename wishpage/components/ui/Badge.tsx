"use client";

import React from "react";
import clsx from "clsx";

type BadgeVariant = "emerald" | "gold" | "red" | "gray" | "dark";

interface BadgeProps {
  variant?: BadgeVariant;
  children: React.ReactNode;
  className?: string;
}

const variantStyles: Record<BadgeVariant, React.CSSProperties> = {
  emerald: {
    background: "rgba(13, 110, 79, 0.10)",
    color: "#0D6E4F",
  },
  gold: {
    background: "rgba(212, 168, 83, 0.10)",
    color: "#D4A853",
  },
  red: {
    background: "rgba(239, 68, 68, 0.10)",
    color: "#EF4444",
  },
  gray: {
    background: "rgba(107, 114, 128, 0.10)",
    color: "#6B7280",
  },
  dark: {
    background: "#0F1A14",
    color: "#FFFFFF",
  },
};

export default function Badge({
  variant = "emerald",
  children,
  className,
}: BadgeProps) {
  return (
    <span
      className={clsx("inline-flex items-center font-medium", className)}
      style={{
        fontFamily: "'DM Sans', sans-serif",
        fontSize: "0.75rem",
        padding: "4px 12px",
        borderRadius: "100px",
        lineHeight: 1.4,
        ...variantStyles[variant],
      }}
    >
      {children}
    </span>
  );
}
