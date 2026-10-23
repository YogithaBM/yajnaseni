/**
 * Web Speech API helpers: locale list, construction and friendly error text.
 * Voice input is an enhancement - typed input always remains available.
 */

export function getSpeechRecognition() {
  if (typeof window === 'undefined') return null;
  return window.SpeechRecognition || window.webkitSpeechRecognition || null;
}

export function isSpeechSupported() {
  return getSpeechRecognition() !== null;
}

/**
 * Maps SpeechRecognitionErrorEvent.error codes to a clear, actionable message.
 * @param {string} code e.g. 'not-allowed', 'no-speech', 'network'
 * @param {string} locale the locale that was requested (used in messages)
 */
export function describeSpeechError(code, locale = 'en-IN') {
  switch (code) {
    case 'not-allowed':
    case 'service-not-allowed':
      return 'Microphone access was blocked. Allow the microphone in your '
        + 'browser settings, or type your answer instead.';
    case 'no-speech':
      return 'No speech was heard. Try again closer to the microphone, or '
        + 'type your answer instead.';
    case 'audio-capture':
      return 'No microphone was found. Check your microphone, or type your '
        + 'answer instead.';
    case 'network':
      return 'The speech service needs a network connection and could not be '
        + 'reached. Type your answer instead.';
    case 'language-not-supported':
      return `This browser does not support voice input for ${locale}. `
        + 'Try another language or type your answer instead.';
    case 'aborted':
      return 'Voice input was stopped.';
    default:
      return `Voice input failed${code ? ` (${code})` : ''}. `
        + 'Type your answer instead.';
  }
}

/** Creates a configured recognition instance (caller starts it). */
export function createRecognition(locale, { onResult, onError, onEnd } = {}) {
  const Ctor = getSpeechRecognition();
  if (!Ctor) return null;
  const recognition = new Ctor();
  recognition.lang = locale;
  recognition.interimResults = false;
  recognition.continuous = false;
  recognition.maxAlternatives = 1;
  if (onResult) {
    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      onResult(transcript);
    };
  }
  if (onError) recognition.onerror = (event) => onError(event.error);
  if (onEnd) recognition.onend = onEnd;
  return recognition;
}
