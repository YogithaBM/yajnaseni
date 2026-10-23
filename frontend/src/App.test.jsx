import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import App from './App.jsx';

const extractionFixture = {
  language_detected: 'hi',
  normalized_text: '3 months se period nahi aaya',
  symptoms: {
    cycle_gap_days: 45,
    months_without_period: 2,
    acne: true,
    excess_facial_or_body_hair: null,
    hair_thinning: false,
    weight_change: 'gain',
    dark_skin_patches: null,
    pelvic_pain: 'mild',
    heavy_bleeding: false,
    possible_pregnancy: false,
  },
  unclear_items: [],
  triage: { level: 'see_doctor_soon', reason: 'Cycles are 45 days apart (more than 35).' },
  source: 'llm',
  fallback_notice: null,
};

const explanationFixture = {
  language_detected: 'hi',
  source: 'llm',
  explanation_source: 'llm',
  explanation: 'What you described shows patterns commonly associated with '
    + 'irregular cycles, which can be related to thyroid issues or stress and '
    + 'is worth discussing with a doctor.',
  disclaimer: 'This is information, not a diagnosis.',
  fallback_notice: null,
};

function mockFetch({ extract, explain } = {}) {
  return vi.fn((url) => {
    if (String(url).includes('/api/extract')) {
      return Promise.resolve({ ok: true, status: 200, json: () => Promise.resolve(extract || extractionFixture) });
    }
    if (String(url).includes('/api/explain')) {
      return Promise.resolve({ ok: true, status: 200, json: () => Promise.resolve(explain || explanationFixture) });
    }
    return Promise.reject(new Error(`unexpected url ${url}`));
  });
}

async function analyzeWith(text) {
  fireEvent.change(screen.getByLabelText('Describe your symptoms'), {
    target: { value: text },
  });
  fireEvent.click(screen.getByRole('button', { name: 'Analyse' }));
  await screen.findByText('See a doctor soon');
}

describe('App', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', mockFetch());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('shows the privacy notice verbatim', () => {
    render(<App />);
    expect(screen.getByTestId('privacy-notice')).toHaveTextContent('No data is stored.');
  });

  it('runs the full flow: triage badge, chips, explanation, disclaimer', async () => {
    render(<App />);
    await analyzeWith('3 mahine se period nahi aaya');

    // Triage badge from backend rules
    expect(screen.getByText('See a doctor soon')).toBeInTheDocument();

    // Symptom chips with AI values
    expect(screen.getByRole('button', { name: /Cycle gap: 45 days/ })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Acne: Yes/ })).toBeInTheDocument();

    // Plain-language explanation
    expect(screen.getByText(/patterns commonly associated with/)).toBeInTheDocument();

    // Disclaimer always present, verbatim
    expect(screen.getByTestId('disclaimer')).toHaveTextContent(
      'This is information, not a diagnosis.',
    );
  });

  it('always shows the disclaimer even when the backend omits it', async () => {
    vi.stubGlobal('fetch', mockFetch({
      explain: { ...explanationFixture, explanation: 'Some short text.', disclaimer: '' },
    }));
    render(<App />);
    await analyzeWith('acne hai');
    expect(screen.getByTestId('disclaimer')).toHaveTextContent(
      'This is information, not a diagnosis.',
    );
  });

  it('shows a clear error when the backend is unreachable', async () => {
    vi.stubGlobal('fetch', vi.fn(() => Promise.reject(new TypeError('Failed to fetch'))));
    render(<App />);
    fireEvent.change(screen.getByLabelText('Describe your symptoms'), {
      target: { value: 'period nahi aaya' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Analyse' }));

    const alert = await screen.findByRole('alert');
    expect(alert).toHaveTextContent(/Cannot reach the Yajnaseni backend/);
    expect(alert).toHaveTextContent(/mvn spring-boot:run/);
  });

  it('surfaces the fallback notice when the backend used the offline extractor', async () => {
    vi.stubGlobal('fetch', mockFetch({
      extract: {
        ...extractionFixture,
        source: 'offline_fallback',
        fallback_notice: 'AI extraction failed, so these answers come from the offline keyword extractor.',
      },
    }));
    render(<App />);
    await analyzeWith('3 mahine se period nahi');
    expect(screen.getByText(/offline keyword extractor/)).toBeInTheDocument();
    expect(screen.getByText('Source: offline keyword extraction')).toBeInTheDocument();
  });

  it('marks a corrected chip as edited', async () => {
    render(<App />);
    await analyzeWith('3 mahine se period nahi aaya');

    fireEvent.click(screen.getByRole('button', { name: /Acne: Yes/ }));
    const group = screen.getByRole('group', { name: 'Acne options' });
    fireEvent.click(screen.getByRole('button', { name: 'No' }));

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Acne: No/ })).toBeInTheDocument();
    });
    expect(screen.getByRole('button', { name: /Acne: No/ })).toHaveClass('chip-edited');
  });

  it('renders the printable doctor summary with required sections', async () => {
    render(<App />);
    await analyzeWith('3 mahine se period nahi aaya');

    expect(screen.getByRole('heading', { name: '1. Confirmed symptoms' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: '2. Cycle data' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: '4. Questions to ask the doctor' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: '5. Tests commonly discussed' })).toBeInTheDocument();
    expect(screen.getByText('Ultrasound (pelvic)')).toBeInTheDocument();
    expect(screen.getByText('Hormone panel')).toBeInTheDocument();
    expect(screen.getByText('Glucose (fasting)')).toBeInTheDocument();
    expect(screen.getByText('HbA1c')).toBeInTheDocument();
  });

  it('computes calendar irregularity in JS without calling the backend', async () => {
    render(<App />);
    // Go to the previous month (fully in the past) and tap two start dates.
    fireEvent.click(screen.getByRole('button', { name: 'Previous month' }));
    const dayButtons = screen.getAllByRole('button').filter((b) =>
      /^\d{4}-\d{2}-\d{2}/.test(b.getAttribute('aria-label') || ''));
    const pick = (dayOfMonth) => dayButtons.find((b) =>
      (b.getAttribute('aria-label') || '').slice(8, 10) === dayOfMonth);
    const third = pick('03');
    const seventeenth = pick('17');
    expect(third, 'day 03 of previous month should be rendered').toBeTruthy();
    expect(seventeenth, 'day 17 of previous month should be rendered').toBeTruthy();
    fireEvent.click(third);
    fireEvent.click(seventeenth);
    expect(screen.getByText(/Cycle pattern: Irregular/)).toBeInTheDocument();
    expect(screen.getAllByText(/less than 21/).length).toBeGreaterThan(0);
  });
});
