import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CalendarPage from './CalendarPage.jsx';
import { AppProvider } from '../context/AppContext.jsx';

function renderCalendarPage() {
  return render(
    <MemoryRouter>
      <AppProvider>
        <CalendarPage />
      </AppProvider>
    </MemoryRouter>
  );
}

describe('CalendarPage', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.stubGlobal('fetch', vi.fn(() => Promise.reject(new Error('Network error'))));
  });

  it('renders the calendar and action buttons', () => {
    renderCalendarPage();
    expect(screen.getByRole('heading', { level: 1, name: /period calendar/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /analyze my cycle/i })).toBeInTheDocument();
  });

  it('opens confirmation modal and clears data on confirm', async () => {
    localStorage.setItem('sakhi_period_dates', JSON.stringify(['2026-04-01', '2026-04-29']));
    renderCalendarPage();

    const clearBtn = screen.getByRole('button', { name: /clear all my data/i });
    expect(clearBtn).not.toBeDisabled();
    fireEvent.click(clearBtn);

    // Modal should be open
    expect(screen.getByText(/clear all saved data\?/i)).toBeInTheDocument();

    const confirmBtn = screen.getByRole('button', { name: /yes, clear all my data/i });
    fireEvent.click(confirmBtn);

    await waitFor(() => {
      expect(localStorage.getItem('sakhi_period_dates')).toBeNull();
    });
  });

  it('analyzes cycle using client fallback when backend is unreachable', async () => {
    // 3 dates = 2 cycles: 28 days and 30 days
    localStorage.setItem(
      'sakhi_period_dates',
      JSON.stringify(['2026-03-01', '2026-03-29', '2026-04-28']),
    );
    renderCalendarPage();

    const analyzeBtn = screen.getByRole('button', { name: /analyze my cycle/i });
    expect(analyzeBtn).not.toBeDisabled();
    fireEvent.click(analyzeBtn);

    await waitFor(() => {
      expect(screen.getByText(/cycle analysis results/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/estimate, cycles vary/i)).toBeInTheDocument();
    expect(screen.getByText(/completed cycles/i)).toBeInTheDocument();
  });

  it('shows need more data message when fewer than 2 cycles are logged', async () => {
    // Only 2 dates = 1 cycle (fewer than 2 cycles)
    localStorage.setItem(
      'sakhi_period_dates',
      JSON.stringify(['2026-04-01', '2026-04-29']),
    );
    renderCalendarPage();

    const analyzeBtn = screen.getByRole('button', { name: /analyze my cycle/i });
    fireEvent.click(analyzeBtn);

    await waitFor(() => {
      expect(screen.getByText(/need more data/i)).toBeInTheDocument();
    });
  });
});
