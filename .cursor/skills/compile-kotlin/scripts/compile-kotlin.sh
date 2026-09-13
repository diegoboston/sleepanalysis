#!/usr/bin/env bash
# Compile :app debug Kotlin and run all JVM unit tests.
# Exits non-zero unless compile succeeds, tests run, and every test passes.
set -euo pipefail

ENV_SH="${ANDROID_BUILD_ENV:-$HOME/tmp/android-build/env.sh}"
if [[ ! -f "$ENV_SH" ]]; then
  echo "compile-kotlin: ERROR — missing build env: $ENV_SH" >&2
  echo "Install JDK 17 + Android SDK under ~/tmp/android-build and create env.sh first." >&2
  echo "This repo is set up to build on GitHub Actions CI." >&2
  exit 1
fi

# shellcheck disable=SC1090
source "$ENV_SH"

if [[ -z "${JAVA_HOME:-}" ]]; then
  echo "compile-kotlin: ERROR — JAVA_HOME is unset after sourcing $ENV_SH" >&2
  exit 1
fi

java_major="$("$JAVA_HOME/bin/java" -version 2>&1 | sed -n 's/.* version "\([0-9]*\).*/\1/p' | head -1)"
if [[ -z "$java_major" || "$java_major" -lt 17 ]]; then
  echo "compile-kotlin: ERROR — Java 17+ required (JAVA_HOME=$JAVA_HOME, major=$java_major)" >&2
  exit 1
fi

ROOT="$(cd "$(dirname "$0")/../../../.." && pwd)"
cd "$ROOT"

echo "compile-kotlin: running :app:compileDebugKotlin :app:testDebugUnitTest (Java $java_major)"
gradle :app:compileDebugKotlin :app:testDebugUnitTest "$@"

RESULTS_DIR="app/build/test-results/testDebugUnitTest"
if [[ ! -d "$RESULTS_DIR" ]]; then
  echo "compile-kotlin: ERROR — $RESULTS_DIR missing; testDebugUnitTest did not run" >&2
  exit 1
fi

shopt -s nullglob
xml_files=("$RESULTS_DIR"/TEST-*.xml)
shopt -u nullglob
if (( ${#xml_files[@]} == 0 )); then
  echo "compile-kotlin: ERROR — no TEST-*.xml under $RESULTS_DIR" >&2
  exit 1
fi

failures=0
errors=0
tests=0
for f in "${xml_files[@]}"; do
  t="$(sed -n 's/.*tests="\([0-9][0-9]*\)".*/\1/p' "$f" | head -1)"
  fa="$(sed -n 's/.*failures="\([0-9][0-9]*\)".*/\1/p' "$f" | head -1)"
  er="$(sed -n 's/.*errors="\([0-9][0-9]*\)".*/\1/p' "$f" | head -1)"
  tests=$((tests + ${t:-0}))
  failures=$((failures + ${fa:-0}))
  errors=$((errors + ${er:-0}))
done

if (( failures > 0 || errors > 0 )); then
  echo "compile-kotlin: ERROR — tests=$tests failures=$failures errors=$errors" >&2
  exit 1
fi

echo "compile-kotlin: VERIFY OK — compileDebugKotlin + testDebugUnitTest ($tests tests, 0 failures)"
