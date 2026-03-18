import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        emerald: {
          DEFAULT: "#0D6E4F",
          light: "#2A8F6A",
          glow: "#34D399",
        },
        gold: {
          DEFAULT: "#D4A853",
          light: "#F0D48A",
        },
        cream: "#FDF8F0",
        dark: {
          DEFAULT: "#0F1A14",
          card: "#142019",
        },
        text: {
          DEFAULT: "#1C1C1C",
          muted: "#6B7280",
        },
      },
      fontFamily: {
        heading: ['"Playfair Display"', "serif"],
        body: ['"DM Sans"', "sans-serif"],
      },
      borderRadius: {
        card: "16px",
        "card-lg": "24px",
        pill: "100px",
      },
      boxShadow: {
        card: "0 2px 20px rgba(0,0,0,0.04)",
        "card-hover": "0 8px 40px rgba(0,0,0,0.08)",
      },
    },
  },
  plugins: [],
};
export default config;
