import { useState } from 'react';
import { CHIPS, formatChipValue } from '../lib/constants.js';

function sameValue(a, b) {
  return a === b || (a === undefined && b === null) || (a === null && b === undefined);
}

function BooleanEditor({ chip, value, onPick }) {
  return (
    <div className="editor-options" role="group" aria-label={`${chip.label} options`}>
      {[true, false, null].map((option) => (
        <button
          key={String(option)}
          type="button"
          className={`option ${sameValue(value, option) ? 'option-current' : ''}`}
          onClick={() => onPick(option)}
        >
          {option === null ? 'Not sure' : option ? 'Yes' : 'No'}
        </button>
      ))}
    </div>
  );
}

function EnumEditor({ chip, value, onPick }) {
  return (
    <div className="editor-options" role="group" aria-label={`${chip.label} options`}>
      {chip.options.map((option) => (
        <button
          key={String(option)}
          type="button"
          className={`option ${sameValue(value, option) ? 'option-current' : ''}`}
          onClick={() => onPick(option)}
        >
          {formatChipValue(chip, option)}
        </button>
      ))}
    </div>
  );
}

function NumberEditor({ chip, value, onApply, onClear }) {
  const [draft, setDraft] = useState(value === null || value === undefined ? '' : String(value));
  return (
    <div className="editor-options">
      <label className="number-editor-label" htmlFor={`editor-${chip.field}`}>
        Days between periods:
      </label>
      <input
        id={`editor-${chip.field}`}
        className="number-input"
        type="number"
        min="1"
        max="180"
        value={draft}
        onChange={(e) => setDraft(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter' && draft !== '') onApply(Number(draft));
        }}
      />
      <button
        type="button"
        className="option"
        disabled={draft === ''}
        onClick={() => onApply(Number(draft))}
      >
        Save
      </button>
      <button type="button" className="option" onClick={onClear}>
        Clear
      </button>
    </div>
  );
}

/**
 * The editable symptom chips. The user taps a chip to confirm the value the
 * AI understood or to correct it.
 *
 * answers: { [field]: { value, status } } with status: 'ai' | 'confirmed' | 'edited'
 * onSet(field, value, 'confirm' | 'edit')
 */
export default function SymptomCard({ answers, onSet, disabled }) {
  const [editing, setEditing] = useState(null);
  const current = answers || {};

  function pick(chip, option) {
    const existing = current[chip.field];
    const action = existing && sameValue(existing.value, option) ? 'confirm' : 'edit';
    onSet(chip.field, option, action);
    setEditing(null);
  }

  return (
    <section className="card" aria-labelledby="symptom-card-title">
      <h2 id="symptom-card-title">What the assistant understood</h2>
      <p className="hint">
        Tap a chip to confirm it or correct it. &quot;Not sure&quot; means the
        assistant did not find a clear answer.
      </p>
      <div className="chips">
        {CHIPS.map((chip) => {
          const entry = current[chip.field] || { value: null, status: 'ai' };
          const isEditing = editing === chip.field;
          return (
            <div className="chip-wrap" key={chip.field}>
              <button
                type="button"
                className={[
                  'chip',
                  entry.status === 'confirmed' ? 'chip-confirmed' : '',
                  entry.status === 'edited' ? 'chip-edited' : '',
                  isEditing ? 'chip-editing' : '',
                ].join(' ')}
                aria-label={`${chip.label}: ${formatChipValue(chip, entry.value)}`}
                aria-pressed={isEditing}
                disabled={disabled}
                onClick={() => setEditing(isEditing ? null : chip.field)}
              >
                <span className="chip-label">{chip.label}</span>
                <span className="chip-value">{formatChipValue(chip, entry.value)}</span>
                {entry.status === 'confirmed' && (
                  <span className="chip-badge" title="You confirmed this">✓</span>
                )}
                {entry.status === 'edited' && (
                  <span className="chip-badge chip-badge-edit" title="You corrected this">
                    edited
                  </span>
                )}
              </button>
              {isEditing && (
                <div className="chip-editor">
                  <p className="editor-title">
                    {chip.label} - what should we record?
                  </p>
                  {chip.type === 'boolean' && (
                    <BooleanEditor chip={chip} value={entry.value} onPick={(v) => pick(chip, v)} />
                  )}
                  {chip.type === 'enum' && (
                    <EnumEditor chip={chip} value={entry.value} onPick={(v) => pick(chip, v)} />
                  )}
                  {chip.type === 'number' && (
                    <NumberEditor
                      chip={chip}
                      value={entry.value}
                      onApply={(v) => pick(chip, v)}
                      onClear={() => pick(chip, null)}
                    />
                  )}
                  <button
                    type="button"
                    className="editor-close"
                    onClick={() => setEditing(null)}
                  >
                    Close
                  </button>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </section>
  );
}
