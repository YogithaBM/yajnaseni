import { useMemo, useState } from 'react';
import * as api from './api.js';
import SymptomCard from './components/SymptomCard.jsx';
import PeriodCalendar from './components/PeriodCalendar.jsx';
import TriageBadge from './components/TriageBadge.jsx';
import VoiceInput from './components/VoiceInput.jsx';
import ExplanationPanel from './components/ExplanationPanel.jsx';
import PrivacyNotice from './components/PrivacyNotice.jsx';
import DoctorSummary from './components/DoctorSummary.jsx';
import { CHIPS, DISCLAIMER, VOICE_LOCALE_LABELS } from './lib/constants.js';
import { computeCycleStats } from './lib/cycleStats.js';
import testInputs from './data/test-inputs.json';

function initAnswers(symptoms) {
  const next = {};
  CHIPS.forEach((chip) => {
    const value = symptoms && symptoms[chip.field] !== undefined
      ? symptoms[chip.field]
      : null;
    next[chip.field] = { value, status: 'ai' };
  });
  return next;
}

const INPUT_GROUPS = testInputs.reduce((groups, input) => {
  (groups[input.language] = groups[input.language] || []).push(input);
  return groups;
}, {});

const GROUP_LABELS = {
  kn: 'Kannada',
  hi: 'Hindi',
  ta: 'Tamil',
  te: 'Telugu',
  hinglish: 'Code-mixed Hinglish',
};

export default function App() {
  const [text, setText] = useState('');
  const [voiceLocale, setVoiceLocale] = useState('hi-IN');
  const [status, setStatus] = useState('idle'); // idle | loading | done | error
  const [error, setError] = useState(null);
  const [extraction, setExtraction] = useState(null);
  const [explanation, setExplanation] = useState(null);
  const [answers, setAnswers] = useState(null);
  const [periodDates, setPeriodDates] = useState([]); // in-memory only

  const cycleStats = useMemo(() => computeCycleStats(periodDates), [periodDates]);

  function appendTranscript(transcript) {
    setText((prev) => (prev.trim() ? `${prev.trim()} ${transcript}` : transcript));
  }

  function handleSet(field, value, action) {
    setAnswers((prev) => ({
      ...prev,
      [field]: { value, status: action === 'confirm' ? 'confirmed' : 'edited' },
    }));
  }

  function toggleDate(iso) {
    setPeriodDates((prev) => {
      if (prev.includes(iso)) return prev.filter((d) => d !== iso);
      return [...prev, iso].sort();
    });
  }

  async function analyze() {
    if (!text.trim()) {
      setError('Type or speak a short description of what you are feeling first.');
      setStatus('error');
      return;
    }
    setStatus('loading');
    setError(null);
    try {
      const [ex, exp] = await Promise.all([api.extract(text), api.explain(text)]);
      setExtraction(ex);
      setExplanation(exp);
      setAnswers(initAnswers(ex.symptoms));
      setStatus('done');
    } catch (requestError) {
      setError(requestError.message);
      setStatus('error');
    }
  }

  return (
    <div className="app">
      <header className="app-header no-print">
        <div className="header-row">
          <h1>Yajnaseni</h1>
          <span className="header-tag">{DISCLAIMER}</span>
        </div>
        <PrivacyNotice />
      </header>

      <main className="columns">
        <div className="col no-print">
          <section className="card" aria-labelledby="input-title">
            <h2 id="input-title">How are you feeling?</h2>
            <p className="hint">
              Type in any language, or use your voice in Kannada, Hindi, Tamil
              or Telugu.
            </p>
            <textarea
              className="input-text"
              rows={5}
              value={text}
              placeholder="Example: 3 mahine se period nahi aaya, acne bhi hai"
              onChange={(e) => setText(e.target.value)}
              aria-label="Describe your symptoms"
            />
            <VoiceInput
              locale={voiceLocale}
              onLocaleChange={setVoiceLocale}
              onTranscript={appendTranscript}
            />
            <div className="sample-row">
              <label htmlFor="sample-input">Try a sample input:</label>
              <select
                id="sample-input"
                value=""
                onChange={(e) => {
                  const chosen = testInputs.find((i) => i.id === e.target.value);
                  if (chosen) setText(chosen.text);
                }}
              >
                <option value="">Choose...</option>
                {Object.keys(INPUT_GROUPS).map((lang) => (
                  <optgroup key={lang} label={GROUP_LABELS[lang] || lang}>
                    {INPUT_GROUPS[lang].map((input) => (
                      <option key={input.id} value={input.id}>
                        {input.title}
                      </option>
                    ))}
                  </optgroup>
                ))}
              </select>
            </div>
            <button
              type="button"
              className="primary-button"
              onClick={analyze}
              disabled={status === 'loading'}
            >
              {status === 'loading' ? 'Analysing...' : 'Analyse'}
            </button>
            {status === 'loading' && (
              <p className="hint" role="status">Asking the assistant...</p>
            )}
            {status === 'error' && error && (
              <p className="error-banner" role="alert">
                {error}
              </p>
            )}
          </section>

          <PeriodCalendar
            dates={periodDates}
            onToggle={toggleDate}
            onClear={() => setPeriodDates([])}
          />
        </div>

        <div className="col">
          {extraction && extraction.fallback_notice && (
            <p className="fallback-notice no-print" role="status">
              {extraction.fallback_notice}
            </p>
          )}
          {extraction && extraction.source === 'offline_fallback' && (
            <p className="source-tag no-print">Source: offline keyword extraction</p>
          )}

          {extraction && <TriageBadge triage={extraction.triage} />}

          <SymptomCard
            answers={answers}
            onSet={handleSet}
            disabled={!answers}
          />

          <ExplanationPanel
            explanation={explanation}
            loading={status === 'loading'}
            error={null}
          />

          <DoctorSummary
            answers={answers}
            cycleStats={cycleStats}
            reportedText={text}
            ready={Boolean(answers)}
          />
        </div>
      </main>

      <footer className="app-footer no-print">
        <span>{DISCLAIMER}</span>
        <span>Voice input locales: hi-IN, kn-IN, ta-IN, te-IN.</span>
        <span>
          Explanation language: {explanation
            ? (VOICE_LOCALE_LABELS[explanation.language_detected]
              || explanation.language_detected)
            : 'after analysis'}
        </span>
      </footer>
    </div>
  );
}
