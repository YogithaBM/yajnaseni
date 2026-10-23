import { describe, expect, it } from 'vitest';
import { render, screen } from '@testing-library/react';
import TriageBadge from './TriageBadge.jsx';

describe('TriageBadge', () => {
  it('shows Emergency', () => {
    render(<TriageBadge triage={{ level: 'emergency', reason: 'Missed period with pregnancy' }} />);
    expect(screen.getByText('Emergency')).toBeInTheDocument();
    expect(screen.getByText(/Missed period with pregnancy/)).toBeInTheDocument();
  });

  it('shows See a doctor soon', () => {
    render(<TriageBadge triage={{ level: 'see_doctor_soon', reason: 'Cycles 45 days apart' }} />);
    expect(screen.getByText('See a doctor soon')).toBeInTheDocument();
  });

  it('shows Routine check-up', () => {
    render(<TriageBadge triage={{ level: 'routine_check_up', reason: 'Single mild symptom' }} />);
    expect(screen.getByText('Routine check-up')).toBeInTheDocument();
  });

  it('shows the cautious default', () => {
    render(<TriageBadge triage={{ level: 'cautious', reason: 'Unclear' }} />);
    expect(screen.getByText('More information needed')).toBeInTheDocument();
  });

  it('renders nothing without a triage result', () => {
    const { container } = render(<TriageBadge triage={null} />);
    expect(container).toBeEmptyDOMElement();
  });
});
