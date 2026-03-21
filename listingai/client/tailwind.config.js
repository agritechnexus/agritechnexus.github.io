/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#FF6B2B',
        'primary-dark': '#E55A1B',
        dark: { 50: '#f8f8f8', 100: '#e0e0e0', 200: '#c0c0c0', 300: '#a0a0a0', 400: '#808080', 500: '#606060', 600: '#404040', 700: '#2a2a2a', 800: '#1a1a1a', 900: '#0f0f0f', 950: '#050505' }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
