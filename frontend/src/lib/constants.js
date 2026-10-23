/** Exact strings required by the spec - keep verbatim. */
export const DISCLAIMER = 'This is information, not a diagnosis.';
export const PRIVACY_NOTICE = 'No data is stored.';

/**
 * The 8 editable symptom chips: label shown to the user + backend field.
 * type: 'boolean' | 'enum' | 'number'
 */
export const CHIPS = [
  { field: 'cycle_gap_days', label: 'Cycle gap', type: 'number', unit: 'days' },
  { field: 'acne', label: 'Acne', type: 'boolean' },
  { field: 'excess_facial_or_body_hair', label: 'Hair growth', type: 'boolean' },
  { field: 'hair_thinning', label: 'Hair thinning', type: 'boolean' },
  { field: 'weight_change', label: 'Weight change', type: 'enum',
    options: ['gain', 'loss', 'none', null] },
  { field: 'dark_skin_patches', label: 'Dark skin patches', type: 'boolean' },
  { field: 'pelvic_pain', label: 'Pain', type: 'enum',
    options: ['none', 'mild', 'severe', null] },
  { field: 'heavy_bleeding', label: 'Bleeding', type: 'boolean' },
];

export const BOOLEAN_OPTIONS = [true, false, null];

/** Backend triage.level -> badge presentation. */
export const TRIAGE_BADGES = {
  emergency: { label: 'Emergency', tone: 'badge-emergency' },
  see_doctor_soon: { label: 'See a doctor soon', tone: 'badge-soon' },
  routine_check_up: { label: 'Routine check-up', tone: 'badge-routine' },
  cautious: { label: 'More information needed', tone: 'badge-cautious' },
};

export const VOICE_LOCALES = (
  (import.meta.env?.VITE_VOICE_LANGS || 'en-IN,hi-IN,kn-IN,ta-IN,te-IN')
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
);

export const VOICE_LOCALE_LABELS = {
  'kn-IN': 'Kannada (kn-IN)',
  'hi-IN': 'Hindi (hi-IN)',
  'ta-IN': 'Tamil (ta-IN)',
  'te-IN': 'Telugu (te-IN)',
  'en-IN': 'English (en-IN)',
};

/** Human-readable rendering of a chip value. */
export function formatChipValue(chip, value) {
  if (value === null || value === undefined) return 'Not sure';
  if (chip.type === 'number') return `${value} ${chip.unit || ''}`.trim();
  if (chip.type === 'boolean') return value ? 'Yes' : 'No';
  if (chip.field === 'weight_change') {
    return value === 'gain' ? 'Gain' : value === 'loss' ? 'Loss' : 'None';
  }
  if (chip.field === 'pelvic_pain') {
    return String(value).charAt(0).toUpperCase() + String(value).slice(1);
  }
  return String(value);
}
