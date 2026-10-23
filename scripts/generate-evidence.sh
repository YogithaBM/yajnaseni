#!/usr/bin/env bash
# Regenerates docs/failure-case-evidence.md by running all 20 test inputs
# against the backend with a failing LLM (offline fallback path).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

PORT="${EVIDENCE_PORT:-8090}"
LOG="${ROOT}/target/evidence-backend.log"
JAR="${ROOT}/target/yajnaseni-0.1.0.jar"

if [ -z "${JAVA_HOME:-}" ]; then
  if command -v java >/dev/null 2>&1; then
    echo "Using java from PATH"
  else
    echo "ERROR: java not found. Install a JDK (8+)." >&2
    exit 1
  fi
else
  JAVA="${JAVA_HOME}/bin/java"
fi
JAVA="${JAVA:-java}"

MVN="${ROOT}/.tools/apache-maven-3.9.9/bin/mvn"
if [ ! -x "$MVN" ]; then
  MVN="$(command -v mvn || true)"
fi

if [ ! -f "$JAR" ] || [ "${REBUILD:-0}" = "1" ]; then
  echo "Building backend jar..."
  if [ -n "${MVN}" ]; then
    "$MVN" -B -DskipTests package -q
  else
    echo "ERROR: no mvn found and no prebuilt jar at $JAR" >&2
    exit 1
  fi
fi

mkdir -p "${ROOT}/target"
echo "Starting backend on port $PORT with sakhi.llm.fail=true ..."
"$JAVA" -jar "$JAR" --server.port="$PORT" --sakhi.llm.fail=true >"$LOG" 2>&1 &
SERVER_PID=$!
cleanup() {
  if kill -0 "$SERVER_PID" 2>/dev/null; then
    kill "$SERVER_PID" 2>/dev/null || true
    wait "$SERVER_PID" 2>/dev/null || true
  fi
}
trap cleanup EXIT

for _ in $(seq 1 45); do
  if curl -sf "http://localhost:$PORT/api/health" >/dev/null 2>&1; then
    break
  fi
  if ! kill -0 "$SERVER_PID" 2>/dev/null; then
    echo "ERROR: backend exited during startup; see $LOG" >&2
    tail -20 "$LOG" >&2 || true
    exit 1
  fi
  sleep 1
done
if ! curl -sf "http://localhost:$PORT/api/health" >/dev/null 2>&1; then
  echo "ERROR: backend did not become healthy; see $LOG" >&2
  exit 1
fi

node "${ROOT}/scripts/generate-evidence.mjs" \
  "http://localhost:$PORT" \
  "${ROOT}/frontend/src/data/test-inputs.json" \
  "${ROOT}/docs/failure-case-evidence.md"

echo "Evidence table written to docs/failure-case-evidence.md"
