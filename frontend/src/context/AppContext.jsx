import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
} from 'react';
import * as api from '../api.js';
import { CHIPS } from '../lib/constants.js';
import { computeCycleStats } from '../lib/cycleStats.js';

/**
 * App-wide state shared by every route: what the user typed, the symptoms the
 * assistant understood (and the user confirmed/edited), the tappable calendar
 * dates and the backend explanation. Living above the router means moving
 * between pages never loses input.
 */
const AppStateContext = createContext(null);

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

export function AppProvider({ children }) {
  const [text, setText] = useState('');
  const [voiceLocale, setVoiceLocale] = useState('hi-IN');
  const [status, setStatus] = useState('idle'); // idle | loading | done | error
  const [error, setError] = useState(null);
  const [extraction, setExtraction] = useState(null);
  const [explanation, setExplanation] = useState(null);
  const [answers, setAnswers] = useState(null);
  const [periodDates, setPeriodDates] = useState([]); // in-memory only

  const cycleStats = useMemo(() => computeCycleStats(periodDates), [periodDates]);

  const appendTranscript = useCallback((transcript) => {
    setText((prev) => (prev.trim() ? `${prev.trim()} ${transcript}` : transcript));
  }, []);

  const setSymptom = useCallback((field, value, action) => {
    setAnswers((prev) => ({
      ...(prev || {}),
      [field]: { value, status: action === 'confirm' ? 'confirmed' : 'edited' },
    }));
  }, []);

  const toggleDate = useCallback((iso) => {
    setPeriodDates((prev) => {
      if (prev.includes(iso)) return prev.filter((d) => d !== iso);
      return [...prev, iso].sort();
    });
  }, []);

  const clearDates = useCallback(() => setPeriodDates([]), []);

  const analyze = useCallback(async (rawText) => {
    const input = typeof rawText === 'string' ? rawText : text;
    if (!input.trim()) {
      setError('Type or speak a short description of what you are feeling first.');
      setStatus('error');
      return;
    }
    setStatus('loading');
    setError(null);
    try {
      const [ex, exp] = await Promise.all([api.extract(input), api.explain(input)]);
      setExtraction(ex);
      setExplanation(exp);
      setAnswers(initAnswers(ex.symptoms));
      setStatus('done');
    } catch (requestError) {
      setError(requestError.message);
      setStatus('error');
    }
  }, [text]);

  const value = useMemo(() => ({
    text,
    setText,
    voiceLocale,
    setVoiceLocale,
    status,
    error,
    extraction,
    explanation,
    answers,
    setSymptom,
    periodDates,
    cycleStats,
    toggleDate,
    clearDates,
    appendTranscript,
    analyze,
  }), [
    text,
    voiceLocale,
    status,
    error,
    extraction,
    explanation,
    answers,
    setSymptom,
    periodDates,
    cycleStats,
    toggleDate,
    clearDates,
    appendTranscript,
    analyze,
  ]);

  return <AppStateContext.Provider value={value}>{children}</AppStateContext.Provider>;
}

export function useAppState() {
  const context = useContext(AppStateContext);
  if (!context) {
    throw new Error('useAppState must be used inside <AppProvider>');
  }
  return context;
}
