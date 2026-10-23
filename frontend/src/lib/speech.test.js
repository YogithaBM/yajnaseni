import { describe, expect, it } from 'vitest';
import { describeSpeechError, getSpeechRecognition, isSpeechSupported } from './speech.js';

describe('describeSpeechError', () => {
  it('explains blocked microphone clearly', () => {
    const message = describeSpeechError('not-allowed');
    expect(message).toMatch(/microphone/i);
    expect(message).toMatch(/type your answer/i);
  });

  it('explains service-not-allowed the same way', () => {
    expect(describeSpeechError('service-not-allowed')).toMatch(/blocked/i);
  });

  it('explains no speech heard', () => {
    expect(describeSpeechError('no-speech')).toMatch(/no speech/i);
  });

  it('explains missing microphone', () => {
    expect(describeSpeechError('audio-capture')).toMatch(/no microphone/i);
  });

  it('explains network problems of the speech service', () => {
    expect(describeSpeechError('network')).toMatch(/network/i);
  });

  it('names the locale for unsupported languages', () => {
    const message = describeSpeechError('language-not-supported', 'kn-IN');
    expect(message).toContain('kn-IN');
    expect(message).toMatch(/type/i);
  });

  it('falls back to a generic message for unknown codes', () => {
    const message = describeSpeechError('weird-code');
    expect(message).toContain('weird-code');
    expect(message).toMatch(/type your answer/i);
  });

  it('always suggests typed input as the fallback', () => {
    for (const code of ['not-allowed', 'no-speech', 'network', 'audio-capture', 'oops']) {
      expect(describeSpeechError(code).toLowerCase()).toContain('type');
    }
  });
});

describe('speech support detection', () => {
  it('does not crash when the Web Speech API is absent (jsdom)', () => {
    expect(getSpeechRecognition()).toBeNull();
    expect(isSpeechSupported()).toBe(false);
  });
});
