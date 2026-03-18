"use client";

import { motion } from "framer-motion";
import { Check } from "lucide-react";

interface StepIndicatorProps {
  currentStep: 1 | 2 | 3;
  steps?: string[];
}

export default function StepIndicator({
  currentStep,
  steps = ["Details", "Content", "Payment"],
}: StepIndicatorProps) {
  return (
    <div className="flex items-center justify-center max-w-md mx-auto py-6">
      {steps.map((label, index) => {
        const stepNum = index + 1;
        const isCompleted = stepNum < currentStep;
        const isActive = stepNum === currentStep;
        const isUpcoming = stepNum > currentStep;

        return (
          <div key={label} className="flex items-center">
            {/* Dot + Label */}
            <div className="flex flex-col items-center">
              <motion.div
                initial={false}
                animate={{
                  scale: isActive ? 1 : 0.9,
                  backgroundColor: isCompleted || isActive ? "#0D6E4F" : "#FFFFFF",
                  borderColor: isUpcoming ? "#D1D5DB" : "#0D6E4F",
                }}
                transition={{ duration: 0.3 }}
                className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold text-sm
                  ${isCompleted || isActive ? "text-white" : "text-gray-400 border-2 border-gray-300 bg-white"}
                  ${isCompleted || isActive ? "border-2 border-emerald bg-emerald" : ""}
                `}
              >
                {isCompleted ? (
                  <motion.div
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ type: "spring", stiffness: 300, damping: 20 }}
                  >
                    <Check size={18} strokeWidth={3} />
                  </motion.div>
                ) : (
                  stepNum
                )}
              </motion.div>
              <span
                className={`text-xs mt-2 ${
                  isCompleted || isActive
                    ? "text-emerald font-semibold"
                    : "text-text-muted"
                }`}
              >
                {label}
              </span>
            </div>

            {/* Line between dots */}
            {index < steps.length - 1 && (
              <div className="w-16 sm:w-24 h-0.5 mx-2 mb-6">
                <motion.div
                  initial={false}
                  animate={{
                    backgroundColor: stepNum < currentStep ? "#0D6E4F" : "#D1D5DB",
                  }}
                  transition={{ duration: 0.3 }}
                  className="h-full w-full rounded-full"
                />
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}
