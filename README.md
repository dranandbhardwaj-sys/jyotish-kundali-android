# Jyotish Kundali

Vedic astrology app: Kundali generation, 12 Rashis and daily Rashifal, North/South Indian charts, Panchang, Graha details and Ashtakoota matching, in Hindi and English.

## Project type

Android app (Kotlin, Gradle). Application ID: `com.aistudio.jyotishkundali.vdqmrx`

## Build

```bash
./gradlew assembleDebug
```

## Setup

- If the app uses Gemini, copy `.env.example` to `.env` and add your own `GEMINI_API_KEY`. Never commit real keys.
- Release builds need your own keystore. Keep it outside the repo and pass `STORE_PASSWORD` / `KEY_PASSWORD` as environment variables.

Related repos: `Jyotish-Kundali-web` (website), `vedic-astro-pro` (placeholder, no code).
