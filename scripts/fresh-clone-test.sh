#!/usr/bin/env bash
# Tests the whole Yajnaseni app from a fresh clone:
#   1. backend unit tests (mvn test)
#   2. frontend install + unit tests + production build
#   3. backend smoke test (/api/health + /api/extract)
#   4. regenerates the failure-case evidence table
# Exits non-zero on the first failure. Requires: JDK 8+, Maven (or the
# vendored .tools/apache-maven-*), Node 18+, npm, python-free (node is used).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

step() { printf '\n=== %s ===\n' "$1"; }

# --- toolchain checks -------------------------------------------------------
step "Toolchain"
command -v node >/dev/null || { echo "ERROR: node not found (need Node 18+)" >&2; exit 1; }
command -v npm  >/dev/null || { echo "ERROR: npm not found" >&2; exit 1; }
echo "node $(node -v), npm $(npm -v)"

if [ -z "${JAVA_HOME:-}" ] && command -v java >/dev/null 2>&1; then
  echo "JAVA_HOME not set; using java from PATH"
fi
MVN="${ROOT}/.tools/apache-maven-3.9.9/bin/mvn"
if [ ! -x "$MVN" ]; then MVN="$(command -v mvn || true)"; fi
[ -n "$MVN" ] || { echo "ERROR: no Maven found (expected .tools/ or mvn in PATH)" >&2; exit 1; }
echo "mvn: $MVN"

# --- backend tests ----------------------------------------------------------
step "Backend tests"
"$MVN" -B test

# --- frontend tests + build -------------------------------------------------
step "Frontend install, tests, build"
cd "$ROOT/frontend"
if [ ! -d node_modules ]; then
  npm install --no-audit --no-fund
fi
npm test
npm run build
cd "$ROOT"

# --- backend smoke test -----------------------------------------------------
step "Backend smoke test"
"$MVN" -B -DskipTests package -q
LOG="${ROOT}/target/smoke-backend.log"
"${JAVA_HOME:+$JAVA_HOME/bin/}java" -jar target/yajnaseni-0.1.0.jar --server.port=8091 >"$LOG" 2>&1 &
SERVER_PID=$!
cleanup() {
  if kill -0 "$SERVER_PID" 2>/dev/null; then
    kill "$SERVER_PID" 2>/dev/null || true
    wait "$SERVER_PID" 2>/dev/null || true
  fi
}
trap cleanup EXIT

for _ in $(seq 1 45); do
  curl -sf http://localhost:8091/api/health >/dev/null 2>&1 && break
  if ! kill -0 "$SERVER_PID" 2>/dev/null; then
    echo "ERROR: backend exited during startup; see $LOG" >&2; tail -20 "$LOG" >&2; exit 1
  fi
  sleep 1
done
curl -sf http://localhost:8091/api/health | grep -q '"status":"ok"'
echo "health: ok"

EXTRACT="$(curl -sf -X POST http://localhost:8091/api/extract \
  -H 'Content-Type: application/json' \
  -d '{"text":"3 mahine se period nahi aaya"}')"
echo "$EXTRACT" | grep -q '"source"'
echo "$EXTRACT" | grep -q '"triage"'
echo "$EXTRACT" | grep -q '"months_without_period":3'
echo "extract: ok (offline number detection, schema + triage present)"

EXPLAIN="$(curl -sf -X POST http://localhost:8091/api/explain \
  -H 'Content-Type: application/json' \
  -d '{"text":"acne hai aur periods late hai"}')"
echo "$EXPLAIN" | grep -q '"disclaimer"'
echo "explain: ok (disclaimer present)"

# --- evidence table ---------------------------------------------------------
step "Evidence table"
bash "${ROOT}/scripts/generate-evidence.sh"

printf '\nALL CHECKS PASSED\n'
