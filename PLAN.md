# Plan

> Source of truth for current goals, decisions, and open questions.
> Updated by `/ship` and by hand. Newest entries at the top of each section.

## Current focus
Phase 1 of multi-stack expansion: restructure to a monorepo (`backend/`, `frontend/`). Phase 2 adds Postgres + Flyway; phase 3 scaffolds Angular 19; phase 4 wires the workflow commands and CI to handle both stacks symmetrically.

## Decisions
_Append-only log of meaningful technical decisions and the reasoning behind them._

- _(none yet — workflow tooling is the only completed work so far)_

## Open questions
_Things we haven't resolved. Move resolved ones into Decisions._

- _(none yet)_

## Changelog
_Short bullet per `/ship`, newest first. Format: `YYYY-MM-DD — <summary>`_

- 2026-05-13 — Established v2 workflow tooling: slash commands (`/plan-feature`, `/ship`, `/verify`, `/merge`, `/integration-verify`, `/promote`, `/address-review`, `/quickfix`), Spotless lint (Google Java Format AOSP), JaCoCo coverage check at 80%, `integration-tests` Maven profile, README.md. Workflow shared via `.claude/` (commands, hook script, project settings.json); `.claude/settings.local.json` stays gitignored.
