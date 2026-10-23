import { DISCLAIMER } from '../lib/constants.js';

/**
 * Plain-language explanation from the backend, shown in the user's language,
 * always accompanied by the fixed disclaimer.
 */
export default function ExplanationPanel({ explanation, loading, error }) {
  return (
    <section className="card" aria-labelledby="explanation-title">
      <h2 id="explanation-title">What this could mean</h2>

      {loading && <p className="hint">Preparing a plain-language explanation...</p>}
      {error && (
        <p className="error-banner" role="alert">
          {error}
        </p>
      )}

      {!loading && !error && !explanation && (
        <p className="hint">
          Enter what you have been experiencing and press Analyse to see an
          explanation in your language.
        </p>
      )}

      {explanation && (
        <>
          {explanation.fallback_notice && (
            <p className="fallback-notice" role="status">
              {explanation.fallback_notice}
            </p>
          )}
          <p className="explanation-text" lang={explanation.language_detected || 'en'}>
            {explanation.explanation}
          </p>
          {explanation.disclaimer &&
            !String(explanation.explanation).includes(explanation.disclaimer) && (
              <p className="localized-disclaimer">
                {explanation.disclaimer}
              </p>
            )}
        </>
      )}

      {/* Always shown, verbatim, regardless of what the backend returned. */}
      <p className="disclaimer" data-testid="disclaimer">
        {DISCLAIMER}
      </p>
    </section>
  );
}
