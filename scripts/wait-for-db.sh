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

DB_USERNAME="${DB_USERNAME:-studentuser}"
DB_NAME="${DB_NAME:-studentdb}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-30}"
SLEEP_SECONDS="${SLEEP_SECONDS:-2}"

if docker compose ps postgres 2>/dev/null | grep -q "(healthy)"; then
  echo "PostgreSQL is already healthy."
  exit 0
fi

echo "Waiting for PostgreSQL to become healthy..."

attempt=1
while [ "$attempt" -le "$MAX_ATTEMPTS" ]; do
  if docker compose ps postgres 2>/dev/null | grep -q "(healthy)"; then
    echo "PostgreSQL is ready."
    exit 0
  fi

  if docker compose exec -T postgres pg_isready -U "$DB_USERNAME" -d "$DB_NAME" >/dev/null 2>&1; then
    echo "PostgreSQL is ready."
    exit 0
  fi

  echo "Attempt ${attempt}/${MAX_ATTEMPTS}: PostgreSQL not ready yet..."
  sleep "$SLEEP_SECONDS"
  attempt=$((attempt + 1))
done

echo "PostgreSQL did not become ready within the expected time."
exit 1
