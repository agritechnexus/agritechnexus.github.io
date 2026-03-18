"use client";

import { useRef, useState } from "react";
import { motion } from "framer-motion";
import { Check } from "lucide-react";

interface ColorThemePickerProps {
  selectedColor: string;
  setSelectedColor: (color: string) => void;
}

const COLORS = [
  { name: "Emerald", value: "#0D6E4F" },
  { name: "Ocean Blue", value: "#1E40AF" },
  { name: "Rose Pink", value: "#E11D48" },
  { name: "Golden Sunset", value: "#D97706" },
  { name: "Purple Night", value: "#7C3AED" },
  { name: "Classic Black", value: "#1C1C1C" },
];

export default function ColorThemePicker({
  selectedColor,
  setSelectedColor,
}: ColorThemePickerProps) {
  const colorInputRef = useRef<HTMLInputElement>(null);
  const [isCustom, setIsCustom] = useState(false);

  const isPreset = COLORS.some((c) => c.value === selectedColor);

  const handlePresetClick = (value: string) => {
    setIsCustom(false);
    setSelectedColor(value);
  };

  const handleCustomClick = () => {
    setIsCustom(true);
    colorInputRef.current?.click();
  };

  return (
    <div>
      <label className="block text-sm font-medium text-text mb-3">
        Color Theme
      </label>
      <div className="flex flex-wrap gap-4 items-start">
        {COLORS.map((color) => {
          const selected = selectedColor === color.value && !isCustom;
          return (
            <div key={color.value} className="flex flex-col items-center">
              <motion.button
                type="button"
                whileHover={{ scale: selected ? 1 : 1.1 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => handlePresetClick(color.value)}
                className={`w-12 h-12 rounded-full cursor-pointer border-2 transition-all flex items-center justify-center ${
                  selected
                    ? "border-text ring-2 ring-offset-2 ring-emerald"
                    : "border-transparent"
                }`}
                style={{ backgroundColor: color.value }}
              >
                {selected && (
                  <motion.div
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ type: "spring", stiffness: 400, damping: 20 }}
                  >
                    <Check size={18} className="text-white" strokeWidth={3} />
                  </motion.div>
                )}
              </motion.button>
              <span className="text-xs text-text-muted mt-1">{color.name}</span>
            </div>
          );
        })}

        {/* Custom color */}
        <div className="flex flex-col items-center">
          <motion.button
            type="button"
            whileHover={{ scale: isCustom && !isPreset ? 1 : 1.1 }}
            whileTap={{ scale: 0.95 }}
            onClick={handleCustomClick}
            className={`w-12 h-12 rounded-full cursor-pointer border-2 transition-all flex items-center justify-center ${
              isCustom
                ? "border-text ring-2 ring-offset-2 ring-emerald"
                : "border-transparent"
            }`}
            style={{
              background: isCustom
                ? selectedColor
                : "conic-gradient(from 0deg, #f00, #ff0, #0f0, #0ff, #00f, #f0f, #f00)",
            }}
          >
            {isCustom && (
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ type: "spring", stiffness: 400, damping: 20 }}
              >
                <Check size={18} className="text-white" strokeWidth={3} />
              </motion.div>
            )}
          </motion.button>
          <span className="text-xs text-text-muted mt-1">Custom</span>
          <input
            ref={colorInputRef}
            type="color"
            value={selectedColor}
            onChange={(e) => {
              setIsCustom(true);
              setSelectedColor(e.target.value);
            }}
            className="sr-only"
          />
        </div>
      </div>
    </div>
  );
}
