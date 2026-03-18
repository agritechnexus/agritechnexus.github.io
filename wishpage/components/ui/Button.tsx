"use client";

import React from "react";
import Link from "next/link";
import clsx from "clsx";

type ButtonVariant = "primary" | "outline" | "ghost" | "gold" | "whatsapp";
type ButtonSize = "sm" | "md" | "lg";

interface ButtonProps {
  variant?: ButtonVariant;
  size?: ButtonSize;
  children: React.ReactNode;
  className?: string;
  onClick?: () => void;
  href?: string;
  disabled?: boolean;
  type?: "button" | "submit" | "reset";
  fullWidth?: boolean;
}

const variantClasses: Record<ButtonVariant, string> = {
  primary:
    "bg-gradient-to-r from-emerald to-emerald-light text-white shadow-[0_4px_14px_rgba(13,110,79,0.35)] hover:shadow-[0_6px_20px_rgba(13,110,79,0.5)] hover:-translate-y-0.5",
  outline:
    "bg-transparent text-emerald border-2 border-emerald hover:bg-emerald hover:text-white",
  ghost: "bg-transparent text-emerald hover:bg-emerald/10",
  gold: "bg-transparent text-gold border-2 border-gold hover:bg-gold hover:text-white",
  whatsapp:
    "bg-[#25D366] text-white shadow-[0_4px_14px_rgba(37,211,102,0.35)] hover:shadow-[0_6px_20px_rgba(37,211,102,0.5)] hover:-translate-y-0.5",
};

const sizeClasses: Record<ButtonSize, string> = {
  sm: "px-4 py-2 text-sm gap-1.5",
  md: "px-6 py-3 text-base gap-2",
  lg: "px-8 py-4 text-lg gap-2.5",
};

export default function Button({
  variant = "primary",
  size = "md",
  children,
  className,
  onClick,
  href,
  disabled = false,
  type = "button",
  fullWidth = false,
}: ButtonProps) {
  const classes = clsx(
    "inline-flex items-center justify-center font-semibold rounded-full transition-all duration-300 cursor-pointer select-none active:scale-[0.97]",
    variantClasses[variant],
    sizeClasses[size],
    fullWidth && "w-full",
    disabled && "opacity-50 cursor-not-allowed pointer-events-none",
    className
  );

  if (href && !disabled) {
    return (
      <Link href={href} className={classes} onClick={onClick as never}>
        {children}
      </Link>
    );
  }

  return (
    <button
      type={type}
      disabled={disabled}
      onClick={onClick}
      className={classes}
    >
      {children}
    </button>
  );
}
