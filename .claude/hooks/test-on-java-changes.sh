#!/usr/bin/env bash
# Runs `mvn test -q` on Stop, but only when there are uncommitted .java changes.
# Invoked by the Stop hook in .claude/settings.local.json.

ROOT=$(git rev-parse --show-toplevel 2>/dev/null) || exit 0
cd "$ROOT/backend" || exit 0

if git diff HEAD --name-only 2>/dev/null | grep -q '\.java$'; then
  mvn test -q
fi
