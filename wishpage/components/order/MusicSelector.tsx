"use client";

import { useRef } from "react";
import { motion } from "framer-motion";
import { Lock, Music } from "lucide-react";

interface MusicSelectorProps {
  selectedMusic: string;
  setSelectedMusic: (music: string) => void;
  isLocked: boolean;
}

const MUSIC_OPTIONS = [
  "No Music",
  "Happy Birthday Classic",
  "Romantic Piano",
  "Festive Celebration",
  "Bollywood Hits Instrumental",
  "Soft Acoustic",
];

const inputClass =
  "w-full px-4 py-3 border border-gray-200 rounded-xl bg-white text-text focus:border-emerald focus:ring-2 focus:ring-emerald/20 outline-none transition";

export default function MusicSelector({
  selectedMusic,
  setSelectedMusic,
  isLocked,
}: MusicSelectorProps) {
  const fileRef = useRef<HTMLInputElement>(null);

  if (isLocked) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <label className="block text-sm font-medium text-text mb-2">
          Background Music
        </label>
        <div className="relative">
          <select disabled className={`${inputClass} bg-gray-100 text-text-muted cursor-not-allowed pr-10`}>
            <option>No Music</option>
          </select>
          <Lock size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-text-muted" />
        </div>
        <p className="text-xs text-text-muted mt-2 flex items-center gap-1">
          <Lock size={12} />
          Upgrade to Premium for music
        </p>
      </motion.div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      <label className="block text-sm font-medium text-text mb-2">
        <Music size={16} className="inline mr-1 mb-0.5" />
        Background Music
      </label>
      <select
        value={selectedMusic}
        onChange={(e) => setSelectedMusic(e.target.value)}
        className={inputClass}
      >
        {MUSIC_OPTIONS.map((opt) => (
          <option key={opt} value={opt}>
            {opt}
          </option>
        ))}
      </select>

      <div className="mt-3">
        <button
          type="button"
          onClick={() => fileRef.current?.click()}
          className="text-sm text-emerald hover:text-emerald-light transition-colors underline underline-offset-2"
        >
          Or upload your own MP3
        </button>
        <input
          ref={fileRef}
          type="file"
          accept=".mp3"
          onChange={(e) => {
            const file = e.target.files?.[0];
            if (file) {
              if (file.size > 5 * 1024 * 1024) {
                alert("File must be under 5MB");
                return;
              }
              setSelectedMusic(`custom:${file.name}`);
            }
          }}
          className="hidden"
        />
      </div>
    </motion.div>
  );
}
