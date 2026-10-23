import { describe, expect, it } from 'vitest';
import inputs from './test-inputs.json';

describe('test inputs fixture', () => {
  it('has exactly 20 inputs', () => {
    expect(inputs).toHaveLength(20);
  });

  it('covers five language groups with four inputs each', () => {
    const groups = {};
    inputs.forEach((i) => { groups[i.language] = (groups[i.language] || 0) + 1; });
    expect(groups).toEqual({
      kn: 4, hi: 4, ta: 4, te: 4, hinglish: 4,
    });
  });

  it('has unique ids and non-empty texts', () => {
    const ids = new Set(inputs.map((i) => i.id));
    expect(ids.size).toBe(20);
    inputs.forEach((i) => {
      expect(i.text.trim().length).toBeGreaterThan(5);
      expect(i.expect.length).toBeGreaterThan(3);
    });
  });

  it('includes every required red-flag case', () => {
    const tags = new Set(inputs.flatMap((i) => i.tags));
    expect(tags).toContain('heavy-bleeding');
    expect(tags).toContain('severe-pain');
    expect(tags).toContain('missed-period');
    expect(tags).toContain('pregnancy');
    expect(tags).toContain('red-flag');
  });

  it('has at least one red-flag case per language', () => {
    ['kn', 'hi', 'ta', 'te', 'hinglish'].forEach((lang) => {
      const redFlags = inputs.filter(
        (i) => i.language === lang && i.tags.includes('red-flag'),
      );
      expect(redFlags.length, `language ${lang}`).toBeGreaterThanOrEqual(1);
    });
  });
});
