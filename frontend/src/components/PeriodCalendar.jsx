import { useState } from 'react';
import { computeCycleStats } from '../lib/cycleStats.js';

function todayIso() {
  const now = new Date();
  const m = String(now.getMonth() + 1).padStart(2, '0');
  const d = String(now.getDate()).padStart(2, '0');
  return `${now.getFullYear()}-${m}-${d}`;
}

function isoOf(year, monthIndex, day) {
  const m = String(monthIndex + 1).padStart(2, '0');
  const d = String(day).padStart(2, '0');
  return `${year}-${m}-${d}`;
}

const WEEKDAYS = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'];
const MONTH_NAMES = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
];

/**
 * Period calendar: the user taps recent period start dates. Cycle regularity
 * is computed deterministically in JS (see lib/cycleStats.js) - no LLM.
 * Dates live in component state only: nothing is stored.
 */
export default function PeriodCalendar({ dates, onToggle, onClear }) {
  const now = new Date();
  const [view, setView] = useState({ year: now.getFullYear(), month: now.getMonth() });

  const selected = new Set(dates);
  const stats = computeCycleStats(dates);
  const today = todayIso();

  const firstWeekday = new Date(view.year, view.month, 1).getDay();
  const daysInMonth = new Date(view.year, view.month + 1, 0).getDate();
  const cells = [];
  for (let i = 0; i < firstWeekday; i += 1) cells.push(null);
  for (let d = 1; d <= daysInMonth; d += 1) cells.push(d);

  function shiftMonth(delta) {
    setView((v) => {
      const next = new Date(v.year, v.month + delta, 1);
      return { year: next.getFullYear(), month: next.getMonth() };
    });
  }

  return (
    <section className="card" aria-labelledby="calendar-title">
      <h2 id="calendar-title">Period calendar</h2>
      <p className="hint">
        Tap the start date of each recent period. Irregularity is calculated
        here in your browser - nothing is stored.
      </p>

      <div className="calendar-nav">
        <button type="button" onClick={() => shiftMonth(-1)} aria-label="Previous month">
          ◀
        </button>
        <span className="calendar-month">
          {MONTH_NAMES[view.month]} {view.year}
        </span>
        <button type="button" onClick={() => shiftMonth(1)} aria-label="Next month">
          ▶
        </button>
      </div>

      <div className="calendar-grid" role="grid" aria-label="Period calendar">
        {WEEKDAYS.map((w) => (
          <span className="calendar-weekday" key={w} role="columnheader">
            {w}
          </span>
        ))}
        {cells.map((day, i) => {
          if (day === null) return <span key={`empty-${i}`} className="calendar-cell-empty" />;
          const iso = isoOf(view.year, view.month, day);
          const isSelected = selected.has(iso);
          const isFuture = iso > today;
          return (
            <button
              key={iso}
              type="button"
              disabled={isFuture}
              className={`calendar-cell ${isSelected ? 'cell-selected' : ''} ${isFuture ? 'cell-future' : ''}`}
              aria-pressed={isSelected}
              aria-label={iso + (isSelected ? ' (period start)' : '')}
              onClick={() => onToggle(iso)}
            >
              {day}
            </button>
          );
        })}
      </div>

      <div className="calendar-stats" aria-live="polite">
        <p className={`verdict ${stats.ready && stats.irregular ? 'verdict-irregular' : ''}`}>
          {stats.ready
            ? `Cycle pattern: ${stats.verdict}`
            : 'Cycle pattern: add at least two dates'}
        </p>
        {stats.ready && (
          <ul className="stats-list">
            <li>Dates selected: {stats.count}</li>
            <li>Gaps (days): {stats.gaps.join(', ')}</li>
            <li>Average gap: {stats.averageGapDays} days</li>
            <li>Shortest / longest: {stats.shortestGapDays} / {stats.longestGapDays} days</li>
          </ul>
        )}
        <ul className="stats-reasons">
          {stats.reasons.map((r) => (
            <li key={r}>{r}</li>
          ))}
        </ul>
        {dates.length > 0 && (
          <button type="button" className="link-button" onClick={onClear}>
            Clear dates
          </button>
        )}
      </div>
    </section>
  );
}
