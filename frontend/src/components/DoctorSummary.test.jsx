import { describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import DoctorSummary from './DoctorSummary.jsx';
import { computeCycleStats } from '../lib/cycleStats.js';

const answers = {
  cycle_gap_days: { value: 45, status: 'confirmed' },
  acne: { value: true, status: 'edited' },
  excess_facial_or_body_hair: { value: null, status: 'ai' },
  hair_thinning: { value: false, status: 'ai' },
  weight_change: { value: 'gain', status: 'ai' },
  dark_skin_patches: { value: null, status: 'ai' },
  pelvic_pain: { value: 'severe', status: 'ai' },
  heavy_bleeding: { value: false, status: 'ai' },
};

const stats = computeCycleStats(['2026-01-01', '2026-02-06']);

describe('DoctorSummary', () => {
  it('lists confirmed symptoms in English', () => {
    render(<DoctorSummary answers={answers} cycleStats={stats} reportedText="x" ready />);
    expect(screen.getByRole('cell', { name: '45 days' })).toBeInTheDocument();
    expect(screen.getByRole('cell', { name: 'Severe' })).toBeInTheDocument();
    expect(screen.getAllByRole('cell', { name: 'Not answered' }).length)
      .toBeGreaterThan(0);
  });

  it('includes cycle data with the irregular verdict', () => {
    render(<DoctorSummary answers={answers} cycleStats={stats} reportedText="x" ready />);
    expect(screen.getByText(/Gaps between periods \(days\): 36/)).toBeInTheDocument();
    expect(screen.getByText(/Pattern: Irregular/)).toBeInTheDocument();
  });

  it('lists questions to ask and the four tests', () => {
    render(<DoctorSummary answers={answers} cycleStats={stats} reportedText="x" ready />);
    expect(screen.getByText(/thyroid issues, pregnancy, anaemia or stress/)).toBeInTheDocument();
    expect(screen.getByText('Ultrasound (pelvic)')).toBeInTheDocument();
    expect(screen.getByText('Hormone panel')).toBeInTheDocument();
    expect(screen.getByText('Glucose (fasting)')).toBeInTheDocument();
    expect(screen.getByText('HbA1c')).toBeInTheDocument();
  });

  it('triggers window.print for the print button', () => {
    const print = vi.fn();
    vi.stubGlobal('print', print);
    render(<DoctorSummary answers={answers} cycleStats={stats} reportedText="x" ready />);
    screen.getByRole('button', { name: 'Print / Save as PDF' }).click();
    expect(print).toHaveBeenCalled();
    vi.unstubAllGlobals();
  });

  it('shows a hint until analysis is done', () => {
    render(<DoctorSummary answers={null} cycleStats={stats} reportedText="" ready={false} />);
    expect(screen.getByText(/Analyse your description first/)).toBeInTheDocument();
  });
});
