import { describe, expect, it } from 'vitest';
import { computeCycleStats, dayDiff } from './cycleStats.js';

describe('computeCycleStats', () => {
  it('cannot decide with no dates', () => {
    const stats = computeCycleStats([]);
    expect(stats.ready).toBe(false);
    expect(stats.verdict).toBe('Not enough dates');
    expect(stats.gaps).toEqual([]);
  });

  it('cannot decide with a single date', () => {
    const stats = computeCycleStats(['2026-01-01']);
    expect(stats.ready).toBe(false);
    expect(stats.count).toBe(1);
  });

  it('reports a regular cycle for steady 28-day gaps', () => {
    const stats = computeCycleStats(['2026-01-01', '2026-01-29', '2026-02-26']);
    expect(stats.ready).toBe(true);
    expect(stats.gaps).toEqual([28, 28]);
    expect(stats.averageGapDays).toBe(28);
    expect(stats.shortestGapDays).toBe(28);
    expect(stats.longestGapDays).toBe(28);
    expect(stats.irregular).toBe(false);
    expect(stats.verdict).toBe('Regular');
  });

  it('treats a 35-day gap as regular (matches backend > 35 rule)', () => {
    const stats = computeCycleStats(['2026-01-01', '2026-02-05']);
    expect(stats.gaps).toEqual([35]);
    expect(stats.irregular).toBe(false);
  });

  it('treats a 36-day gap as irregular', () => {
    const stats = computeCycleStats(['2026-01-01', '2026-02-06']);
    expect(stats.gaps).toEqual([36]);
    expect(stats.irregular).toBe(true);
    expect(stats.reasons[0]).toContain('36 days');
  });

  it('treats a 20-day gap as too short and 21 as fine', () => {
    expect(computeCycleStats(['2026-01-01', '2026-01-22']).irregular).toBe(false);
    expect(computeCycleStats(['2026-01-01', '2026-01-21']).irregular).toBe(true);
  });

  it('sorts and deduplicates input dates', () => {
    const stats = computeCycleStats(['2026-02-26', '2026-01-01', '2026-01-01']);
    expect(stats.count).toBe(2);
    expect(stats.dates).toEqual(['2026-01-01', '2026-02-26']);
    expect(stats.gaps).toEqual([56]);
  });

  it('is deterministic - same input, same output', () => {
    const input = ['2026-03-01', '2026-03-29', '2026-04-30'];
    expect(computeCycleStats(input)).toEqual(computeCycleStats([...input]));
  });
});

describe('dayDiff', () => {
  it('counts days across month boundaries', () => {
    expect(dayDiff('2026-01-31', '2026-02-01')).toBe(1);
    expect(dayDiff('2026-12-31', '2027-01-01')).toBe(1);
  });
});
