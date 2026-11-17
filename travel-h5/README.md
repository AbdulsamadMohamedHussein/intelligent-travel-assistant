# Travel Assistant H5 (Vue 3 + Vant)

Front end of the Travel Assistant. Talks to the Spring Boot backend (`travel-backend`, port 3200).

## Run
1. Start the backend first (http://localhost:3200/api/travel/hello must work).
2. In this folder: `npm install` then `npm run dev`, and open the address shown (usually http://localhost:5173).
3. The backend address is set in `.env` (`VITE_API_BASE=http://localhost:3200`).

## Pages
- **Home**: choose city, budget and days (cities come from the backend) and start planning
- **Detail**: day-by-day plan, budget breakdown, tips and warnings; Save Trip; Ask AI Assistant
- **Chat**: streaming chat with the travel assistant
- **Profile**: saved trips (tap to open, swipe left to delete)

Key files: `src/utils/request.js` (axios + anonymous user id), `src/utils/api.js` (all API calls and the SSE stream reader).
