import { PRIVACY_NOTICE } from '../lib/constants.js';

/** Persistent privacy banner - exact spec text is rendered verbatim. */
export default function PrivacyNotice() {
  return (
    <div className="privacy-notice" role="note">
      <span className="privacy-lock" aria-hidden="true">🔒</span>
      <span>
        <strong data-testid="privacy-notice">{PRIVACY_NOTICE}</strong>{' '}
        Your text, voice and calendar dates stay in this browser tab and are
        never saved. Only the text you choose to analyse is sent to the backend,
        and the backend keeps no copy.
      </span>
    </div>
  );
}
