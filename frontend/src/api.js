const BASE = (import.meta.env?.VITE_API_BASE || 'http://localhost:8080').replace(/\/+$/, '');

export const API_BASE = BASE;

async function postJson(path, body) {
  let response;
  try {
    response = await fetch(`${BASE}${path}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });
  } catch (networkError) {
    throw new Error(
      `Cannot reach the Yajnaseni backend at ${BASE}. `
      + 'Start it with "mvn spring-boot:run" (or java -jar target/yajnaseni-0.1.0.jar).',
    );
  }
  if (!response.ok) {
    let detail = '';
    try {
      const payload = await response.json();
      detail = payload.error ? `: ${payload.error}` : '';
    } catch {
      /* body was not JSON */
    }
    throw new Error(`Backend error ${response.status}${detail}`);
  }
  return response.json();
}

/** POST /api/extract -> { language_detected, symptoms, triage, source, ... } */
export function extract(text) {
  return postJson('/api/extract', { text });
}

/** POST /api/explain -> { explanation, disclaimer, source, ... } */
export function explain(text) {
  return postJson('/api/explain', { text });
}

/** Convenience: run both calls in parallel. */
export async function analyze(text) {
  const [extraction, explanation] = await Promise.all([extract(text), explain(text)]);
  return { extraction, explanation };
}
