# ListingAI — CLAUDE.md

## Project
AI-powered e-commerce product listing generator for Indian marketplace sellers.

## Stack
React 18 + Vite + Tailwind | Node.js + Express | MongoDB | Claude API | Razorpay

## Commands
- `cd client && npm run dev` — Start frontend (port 3000)
- `cd server && npm run dev` — Start backend (port 5000)
- `docker-compose up` — Start everything

## Architecture
- All Claude API calls go through `server/services/claude.service.js`
- Prompt templates in `server/utils/prompts.js`
- Marketplace rules/limits defined per-platform
- Usage enforcement in middleware, not in route handlers

## Key Patterns
- Server-side only for Claude API (never expose key to frontend)
- JSON response parsing with regex fallback
- Streaming for single generation, batch for bulk
- Plan-based model selection (Sonnet for free/starter, Opus for pro/agency)

## Current Focus
Building MVP with single listing generation + landing page demo
