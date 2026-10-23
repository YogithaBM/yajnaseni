import { TRIAGE_BADGES } from '../lib/constants.js';

/**
 * Triage badge driven by the backend rules:
 * Emergency / See a doctor soon / Routine check-up (plus the cautious default).
 */
export default function TriageBadge({ triage }) {
  if (!triage || !triage.level) return null;
  const config = TRIAGE_BADGES[triage.level] || {
    label: triage.level,
    tone: 'badge-cautious',
  };
  return (
    <section className={`triage-badge ${config.tone}`} aria-labelledby="triage-title">
      <h2 id="triage-title" className="visually-hidden">Triage result</h2>
      <span className="triage-label" role="status">
        {config.label}
      </span>
      {triage.reason && <p className="triage-reason">{triage.reason}</p>}
    </section>
  );
}
