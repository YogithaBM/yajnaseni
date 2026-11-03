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
import { getTranslation } from '../data/translations.js';

/**
 * App-wide state shared by every route: what the user typed, the symptoms the
 * assistant understood (and the user confirmed/edited), the tappable calendar
 * dates (stored in localStorage) and the backend explanation. Living above the
 * router means moving between pages never loses input.
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
  const [lang, setLangState] = useState(() => {
    try {
      return localStorage.getItem('sakhi_lang') || 'en';
    } catch (e) {
      return 'en';
    }
  });

  const setLang = useCallback((nextLang) => {
    setLangState(nextLang);
    try {
      localStorage.setItem('sakhi_lang', nextLang);
    } catch (e) {
      // ignore
    }
  }, []);

  const t = useCallback((key) => getTranslation(lang, key), [lang]);

  const [text, setText] = useState('');
  const [voiceLocale, setVoiceLocale] = useState('hi-IN');
  const [status, setStatus] = useState('idle'); // idle | loading | done | error
  const [error, setError] = useState(null);
  const [extraction, setExtraction] = useState(null);
  const [explanation, setExplanation] = useState(null);
  const [answers, setAnswers] = useState(null);

  // Period dates: in browser localStorage only, never sent to server for storage
  const [periodDates, setPeriodDates] = useState(() => {
    try {
      const saved = localStorage.getItem('sakhi_period_dates');
      return saved ? JSON.parse(saved) : [];
    } catch (e) {
      return [];
    }
  });

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
      const next = prev.includes(iso) ? prev.filter((d) => d !== iso) : [...prev, iso].sort();
      try {
        localStorage.setItem('sakhi_period_dates', JSON.stringify(next));
      } catch (e) {
        // ignore
      }
      return next;
    });
  }, []);

  const clearDates = useCallback(() => {
    setPeriodDates([]);
    try {
      localStorage.removeItem('sakhi_period_dates');
    } catch (e) {
      // ignore
    }
  }, []);

  const clearAllData = useCallback(() => {
    setText('');
    setStatus('idle');
    setError(null);
    setExtraction(null);
    setExplanation(null);
    setAnswers(null);
    setPeriodDates([]);
    try {
      localStorage.removeItem('sakhi_period_dates');
    } catch (e) {
      // ignore
    }
  }, []);

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
    lang,
    setLang,
    t,
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
    clearAllData,
    appendTranscript,
    analyze,
  }), [
    lang,
    setLang,
    t,
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
    clearAllData,
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
