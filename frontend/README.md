# Sakhi — Women's Health Symptom Guide & Period Tracker Frontend

React 18 (Vite 5) responsive application for Sakhi — an empathetic, quiet, and private symptom guide and menstrual tracker.

## Pages & Routes

1. **Home (`/`)**:
   - Welcome & purpose of Sakhi.
   - Quick action buttons: *"Check my symptoms"*, *"Track my period"*, *"Doctor summary"*.
   - Prominent not-a-diagnosis alert (*"This is information, not a diagnosis"*).
   - Active session snapshot and 4-step walkthrough.
2. **Check Symptoms (`/symptoms`)**:
   - Free-form symptom description via typing or Web Speech voice input (Kannada, Hindi, Tamil, Telugu, and English).
   - 20 realistic sample inputs grouped by language.
   - Interactive confirmed/edited symptom chips with 44px touch targets.
   - Live rule-based triage badge (Emergency / See a doctor soon / Routine check-up / Cautious).
   - Plain-language AI explanation in user's language.
   - Immediate printable doctor summary card.
3. **Period Calendar (`/calendar`)**:
   - Month grid with previous/next navigation, today highlighted, and future dates disabled.
   - Period start dates saved strictly in user's browser `localStorage` (`sakhi_period_dates`) — never sent to a server for storage.
   - *"Analyze my cycle"* action calling `POST /api/cycle/analyze` (with automatic client fallback if backend is offline).
   - Displays completed cycles, average cycle length, shortest/longest cycle, irregularity flag ($< 21$ or $> 35$ days), and estimated next period window labeled *"estimate, cycles vary"*.
   - *"Need more data"* notice when fewer than 2 cycles are logged.
   - *"Clear all my data"* action with confirmation modal.
4. **Doctor Summary (`/summary`)**:
   - Printable clinical summary designed to bring to a gynecologist or doctor consultation.
   - Lists confirmed symptoms, cycle statistics, questions to ask, and standard lab tests commonly discussed (ultrasound, hormone panel, glucose, HbA1c).
   - Clean `@media print` stylesheet that automatically hides navigation, drawers, headers, and buttons during print.
5. **About and Safety (`/about`)**:
   - Detailed guide explaining how Sakhi works and what it does *not* do (no diagnosis, no prescriptions).
   - Privacy guarantee (*"No data is stored"*).
   - Urgent red flags guide (when to seek emergency medical care).
   - 24x7 India emergency and health helplines directory (112, 1091, 181, Tele-MANAS 14416, KIRAN 1800-599-0019).

## Navigation & Language Support

- **Responsive Header**:
  - $\ge 768\text{px}$: Top horizontal navigation bar.
  - $< 768\text{px}$: Accessible hamburger button opening a slide-in drawer menu.
  - Closes on link tap, outside backdrop tap, and `Escape` key press.
  - Fully accessible with `aria-label`s.
- **Language Switcher**:
  - Switch UI language between English, Hindi (हिंदी), and Kannada (ಕನ್ನಡ) via `src/data/translations.js`.
  - Persists preference in `localStorage`.

## Run steps

Prerequisites: Node 18+ and the backend running on `:8080`.

```bash
cd frontend
cp .env.example .env        # optional; defaults work out of the box
npm install
npm run dev                 # http://localhost:5173
```

In another terminal, start the backend from the repository root:

```bash
mvn spring-boot:run         # or .tools/apache-maven-3.9.9/bin/mvn spring-boot:run
```

## Tests

```bash
npm test          # 55+ unit/component tests (Vitest + Testing Library)
npm run build     # production build -> dist/
```
