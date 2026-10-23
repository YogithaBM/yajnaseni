/**
 * Deterministic cycle-irregularity computation (no LLM).
 *
 * Input: ISO date strings (YYYY-MM-DD) of period start dates the user tapped.
 * Output: gaps between consecutive starts and a regular/irregular verdict.
 *
 * Rules (mirroring the backend triage table):
 * - a gap of 21..35 days (inclusive) is regular
 * - any gap > 35 days or < 21 days makes the cycle irregular
 * - fewer than 2 dates cannot produce a verdict
 */

export const MIN_REGULAR_GAP_DAYS = 21;
export const MAX_REGULAR_GAP_DAYS = 35;

export function toUtcMillis(isoDate) {
  const [y, m, d] = isoDate.split('-').map(Number);
  return Date.UTC(y, m - 1, d);
}

export function dayDiff(isoEarlier, isoLater) {
  return Math.round((toUtcMillis(isoLater) - toUtcMillis(isoEarlier)) / 86400000);
}

export function normalizeDates(isoDates) {
  const unique = [...new Set((isoDates || []).filter(Boolean))];
  unique.sort((a, b) => toUtcMillis(a) - toUtcMillis(b));
  return unique;
}

/**
 * @param {string[]} isoDates period start dates (YYYY-MM-DD)
 * @returns {object} stats incl. verdict; `ready` is false with < 2 dates
 */
export function computeCycleStats(isoDates) {
  const dates = normalizeDates(isoDates);
  const base = { count: dates.length, dates, gaps: [] };

  if (dates.length < 2) {
    return {
      ...base,
      ready: false,
      averageGapDays: null,
      shortestGapDays: null,
      longestGapDays: null,
      irregular: false,
      verdict: 'Not enough dates',
      reasons: ['Tap at least two period start dates to compute cycle regularity.'],
    };
  }

  const gaps = [];
  for (let i = 1; i < dates.length; i += 1) {
    gaps.push(dayDiff(dates[i - 1], dates[i]));
  }

  const sum = gaps.reduce((acc, g) => acc + g, 0);
  const averageGapDays = Math.round((sum / gaps.length) * 10) / 10;
  const shortestGapDays = Math.min(...gaps);
  const longestGapDays = Math.max(...gaps);

  const reasons = [];
  gaps.forEach((g, i) => {
    const prev = dates[i];
    const next = dates[i + 1];
    if (g > MAX_REGULAR_GAP_DAYS) {
      reasons.push(`${prev} to ${next} is ${g} days (more than ${MAX_REGULAR_GAP_DAYS})`);
    } else if (g < MIN_REGULAR_GAP_DAYS) {
      reasons.push(`${prev} to ${next} is ${g} days (less than ${MIN_REGULAR_GAP_DAYS})`);
    }
  });

  const irregular = reasons.length > 0;
  return {
    ...base,
    ready: true,
    gaps,
    averageGapDays,
    shortestGapDays,
    longestGapDays,
    irregular,
    verdict: irregular ? 'Irregular' : 'Regular',
    reasons: irregular
      ? reasons
      : [`All ${gaps.length} gaps are between ${MIN_REGULAR_GAP_DAYS} and `
        + `${MAX_REGULAR_GAP_DAYS} days.`],
  };
}
