#!/usr/bin/env bash
set -euo pipefail

SEED_REPO_DIR="${SEED_REPO_DIR:-/opt/m2-seed/repository}"
TARGET_REPO_DIR="${TARGET_REPO_DIR:-/root/.m2/repository}"
SEED_MARKER="${TARGET_REPO_DIR}/.seeded-from-test-automation-fwk"

if [ "${SEED_M2_REPO:-true}" = "true" ] && [ -d "${SEED_REPO_DIR}" ]; then
  mkdir -p "${TARGET_REPO_DIR}"
  if [ ! -f "${SEED_MARKER}" ]; then
    echo "[test-runner] Seeding Maven repository cache from test-automation-fwk base image"
    cp -a "${SEED_REPO_DIR}/." "${TARGET_REPO_DIR}/"
    touch "${SEED_MARKER}"
  fi
fi

WORKSPACE_DIR="${WORKSPACE_DIR:-/workspace}"
cd "${WORKSPACE_DIR}"

if [ "$#" -eq 0 ]; then
  echo "No command provided. Example: mvn -B -f test-testng-framework/pom.xml test"
  exec bash
fi

echo "[test-runner] Working directory: $(pwd)"
echo "[test-runner] Executing: $*"
exec "$@"
