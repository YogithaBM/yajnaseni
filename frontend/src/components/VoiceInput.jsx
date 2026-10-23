import { useEffect, useRef, useState } from 'react';
import {
  VOICE_LOCALES,
  VOICE_LOCALE_LABELS,
} from '../lib/constants.js';
import {
  createRecognition,
  describeSpeechError,
  isSpeechSupported,
} from '../lib/speech.js';

/**
 * Browser voice input (Web Speech API) for kn-IN, hi-IN, ta-IN and te-IN.
 * Typed input always remains available as the fallback; every failure mode
 * surfaces a clear, actionable message.
 */
export default function VoiceInput({ locale, onLocaleChange, onTranscript }) {
  const [listening, setListening] = useState(false);
  const [error, setError] = useState(null);
  const recognitionRef = useRef(null);
  const supported = isSpeechSupported();

  useEffect(() => () => {
    if (recognitionRef.current) {
      try { recognitionRef.current.abort(); } catch { /* already stopped */ }
    }
  }, []);

  function start() {
    setError(null);
    const recognition = createRecognition(locale, {
      onResult: (transcript) => {
        onTranscript(transcript);
        setListening(false);
      },
      onError: (code) => {
        setError(describeSpeechError(code, locale));
        setListening(false);
      },
      onEnd: () => setListening(false),
    });
    if (!recognition) {
      setError(describeSpeechError('unsupported', locale));
      return;
    }
    recognitionRef.current = recognition;
    try {
      recognition.start();
      setListening(true);
    } catch (startError) {
      setError(describeSpeechError('aborted', locale));
      setListening(false);
    }
  }

  function stop() {
    if (recognitionRef.current) recognitionRef.current.stop();
    setListening(false);
  }

  return (
    <div className="voice-block">
      <div className="voice-row">
        <label htmlFor="voice-locale">Voice language</label>
        <select
          id="voice-locale"
          value={locale}
          onChange={(e) => onLocaleChange(e.target.value)}
        >
          {VOICE_LOCALES.map((code) => (
            <option key={code} value={code}>
              {VOICE_LOCALE_LABELS[code] || code}
            </option>
          ))}
        </select>
        <button
          type="button"
          className={`voice-button ${listening ? 'voice-listening' : ''}`}
          onClick={listening ? stop : start}
          disabled={!supported}
        >
          {listening ? 'Stop listening' : 'Start voice input'}
        </button>
      </div>
      {!supported && (
        <p className="voice-warning" role="status">
          Voice input is not supported in this browser. Please type your answer
          in the box below.
        </p>
      )}
      {listening && (
        <p className="voice-status" role="status">
          Listening in {locale}... speak now.
        </p>
      )}
      {error && (
        <p className="voice-error" role="alert">
          {error}
        </p>
      )}
    </div>
  );
}
