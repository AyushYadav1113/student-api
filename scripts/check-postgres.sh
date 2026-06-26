#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if [ -f .env ]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

if docker compose ps postgres 2>/dev/null | grep -q "Up"; then
  echo "PostgreSQL container is already running."
  exit 0
fi

echo "PostgreSQL container is not running."
exit 1
