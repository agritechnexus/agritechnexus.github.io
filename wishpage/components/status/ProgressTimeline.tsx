"use client";

import { motion } from "framer-motion";
import { CreditCard, Sparkles, Globe, PartyPopper, Check } from "lucide-react";
import { LucideIcon } from "lucide-react";

type Status = "PAID" | "GENERATING" | "DEPLOYING" | "DELIVERED" | "FAILED";

interface Step {
  label: string;
  subtitle: string;
  icon: LucideIcon;
}

interface ProgressTimelineProps {
  currentStatus: Status;
}

const steps: Step[] = [
  { label: "Payment Confirmed", subtitle: "₹499 received", icon: CreditCard },
  {
    label: "Generating Your Site",
    subtitle: "Our AI is crafting something beautiful...",
    icon: Sparkles,
  },
  {
    label: "Deploying to Web",
    subtitle: "Making it live for the world...",
    icon: Globe,
  },
  {
    label: "Your Site is Ready!",
    subtitle: "Time to share the joy!",
    icon: PartyPopper,
  },
];

function getStepState(
  stepIndex: number,
  currentStatus: Status
): "completed" | "active" | "upcoming" {
  const statusMap: Record<Status, { completed: number[]; active: number | null }> = {
    PAID: { completed: [0], active: 1 },
    GENERATING: { completed: [0, 1], active: 2 },
    DEPLOYING: { completed: [0, 1], active: 2 },
    DELIVERED: { completed: [0, 1, 2, 3], active: null },
    FAILED: { completed: [0], active: null },
  };

  const mapping = statusMap[currentStatus];

  if (mapping.completed.includes(stepIndex)) return "completed";
  if (mapping.active === stepIndex) return "active";
  return "upcoming";
}

export default function ProgressTimeline({ currentStatus }: ProgressTimelineProps) {
  return (
    <div className="flex flex-col space-y-0">
      {steps.map((step, index) => {
        const state = getStepState(index, currentStatus);
        const isLast = index === steps.length - 1;
        const Icon = step.icon;

        return (
          <div key={index} className="flex items-start gap-4">
            {/* Left column: circle + line */}
            <div className="flex flex-col items-center">
              <motion.div
                initial={{ scale: 0.8, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                transition={{ delay: index * 0.15, duration: 0.3 }}
                className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 ${
                  state === "completed"
                    ? "bg-emerald text-white"
                    : state === "active"
                    ? "bg-emerald text-white ring-4 ring-emerald/30 animate-pulse"
                    : "bg-gray-100 text-gray-400"
                }`}
              >
                {state === "completed" ? (
                  <motion.div
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{
                      type: "spring",
                      stiffness: 300,
                      damping: 20,
                      delay: index * 0.15 + 0.1,
                    }}
                  >
                    <Check className="w-5 h-5" />
                  </motion.div>
                ) : state === "active" ? (
                  <div className="border-2 border-white border-t-transparent rounded-full w-5 h-5 animate-spin" />
                ) : (
                  <Icon className="w-5 h-5" />
                )}
              </motion.div>

              {/* Vertical line */}
              {!isLast && (
                <div
                  className={`w-0.5 h-12 mx-auto ${
                    state === "completed" ? "bg-emerald" : "bg-gray-200"
                  }`}
                />
              )}
            </div>

            {/* Right column: text */}
            <motion.div
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.15 + 0.05, duration: 0.3 }}
              className="pt-2"
            >
              <p
                className={`font-semibold ${
                  state === "upcoming" ? "text-text-muted" : "text-text"
                }`}
              >
                {step.label}
              </p>
              <p className="text-sm text-text-muted">
                {step.subtitle}
                {state === "active" && (
                  <span className="inline-block animate-pulse ml-1">...</span>
                )}
              </p>
            </motion.div>
          </div>
        );
      })}
    </div>
  );
}
