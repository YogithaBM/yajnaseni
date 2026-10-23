import { describe, expect, it, vi } from 'vitest';
import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import SymptomCard from './SymptomCard.jsx';

function makeAnswers() {
  return {
    cycle_gap_days: { value: 45, status: 'ai' },
    acne: { value: true, status: 'ai' },
    excess_facial_or_body_hair: { value: null, status: 'ai' },
    hair_thinning: { value: false, status: 'ai' },
    weight_change: { value: 'gain', status: 'ai' },
    dark_skin_patches: { value: null, status: 'ai' },
    pelvic_pain: { value: 'mild', status: 'ai' },
    heavy_bleeding: { value: false, status: 'ai' },
  };
}

describe('SymptomCard', () => {
  it('renders one chip per symptom with the AI values', () => {
    render(<SymptomCard answers={makeAnswers()} onSet={() => {}} />);
    expect(screen.getByRole('button', { name: /Cycle gap: 45 days/ })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Acne: Yes/ })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Hair growth: Not sure/ })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Pain: Mild/ })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Weight change: Gain/ })).toBeInTheDocument();
  });

  it('marks a corrected value as edited', async () => {
    const user = userEvent.setup();
    const onSet = vi.fn();
    render(<SymptomCard answers={makeAnswers()} onSet={onSet} />);

    await user.click(screen.getByRole('button', { name: /Acne: Yes/ }));
    const group = screen.getByRole('group', { name: 'Acne options' });
    await user.click(within(group).getByRole('button', { name: 'No' }));

    expect(onSet).toHaveBeenCalledWith('acne', false, 'edit');
  });

  it('marks re-selecting the same value as a confirmation', async () => {
    const user = userEvent.setup();
    const onSet = vi.fn();
    render(<SymptomCard answers={makeAnswers()} onSet={onSet} />);

    await user.click(screen.getByRole('button', { name: /Acne: Yes/ }));
    const group = screen.getByRole('group', { name: 'Acne options' });
    await user.click(within(group).getByRole('button', { name: 'Yes' }));

    expect(onSet).toHaveBeenCalledWith('acne', true, 'confirm');
  });

  it('lets the user set "Not sure"', async () => {
    const user = userEvent.setup();
    const onSet = vi.fn();
    render(<SymptomCard answers={makeAnswers()} onSet={onSet} />);

    await user.click(screen.getByRole('button', { name: /Hair thinning: No/ }));
    const group = screen.getByRole('group', { name: 'Hair thinning options' });
    await user.click(within(group).getByRole('button', { name: 'Not sure' }));

    expect(onSet).toHaveBeenCalledWith('hair_thinning', null, 'edit');
  });

  it('edits the cycle gap as a number', async () => {
    const user = userEvent.setup();
    const onSet = vi.fn();
    render(<SymptomCard answers={makeAnswers()} onSet={onSet} />);

    await user.click(screen.getByRole('button', { name: /Cycle gap: 45 days/ }));
    const input = screen.getByLabelText('Days between periods:');
    await user.clear(input);
    await user.type(input, '30');
    await user.click(screen.getByRole('button', { name: 'Save' }));

    expect(onSet).toHaveBeenCalledWith('cycle_gap_days', 30, 'edit');
  });

  it('shows confirmed and edited badges', () => {
    const answers = makeAnswers();
    answers.acne = { value: true, status: 'confirmed' };
    answers.hair_thinning = { value: false, status: 'edited' };
    render(<SymptomCard answers={answers} onSet={() => {}} />);

    const acneChip = screen.getByRole('button', { name: /Acne: Yes/ });
    expect(within(acneChip).getByTitle('You confirmed this')).toBeInTheDocument();
    const hairChip = screen.getByRole('button', { name: /Hair thinning: No/ });
    expect(within(hairChip).getByTitle('You corrected this')).toBeInTheDocument();
  });
});
