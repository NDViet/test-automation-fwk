#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
IMAGE_TAG="${1:-ndviet/test-automation-java-common:local}"
JAVA_BASE_IMAGE="${JAVA_BASE_IMAGE:-ndviet/test-automation-java-base:latest}"

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is required but was not found in PATH"
  exit 1
fi

echo "[container] Building base image from test-automation-fwk -> ${IMAGE_TAG}"
echo "[container] Using parent base image: ${JAVA_BASE_IMAGE}"
docker build \
  -f "${SCRIPT_DIR}/base-test-runner/Dockerfile" \
  --build-arg "JAVA_BASE_IMAGE=${JAVA_BASE_IMAGE}" \
  -t "${IMAGE_TAG}" \
  "${PROJECT_ROOT}"

echo "[container] Base image build completed: ${IMAGE_TAG}"
