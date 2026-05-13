# Plan

> Source of truth for current goals, decisions, and open questions.
> Updated by `/ship` and by hand. Newest entries at the top of each section.

## Current focus
This repo is now the `claude-base-monorepo` template — a snapshot of the workflow + scaffolding intended as a starting point for future projects. New projects branch off this state and rename via the recipe in [CLAUDE.md](CLAUDE.md) → "Renaming for a new project". Future work in *this* repo is improvements to the template itself.

## Decisions
_Append-only log of meaningful technical decisions and the reasoning behind them._

- _(none yet — workflow tooling is the only completed work so far)_

## Open questions
_Things we haven't resolved. Move resolved ones into Decisions._

- _(none yet)_

## Changelog
_Short bullet per `/ship`, newest first. Format: `YYYY-MM-DD — <summary>`_

- 2026-05-13 — Established v2 workflow tooling: slash commands (`/plan-feature`, `/ship`, `/verify`, `/merge`, `/integration-verify`, `/promote`, `/address-review`, `/quickfix`), Spotless lint (Google Java Format AOSP), JaCoCo coverage check at 80%, `integration-tests` Maven profile, README.md. Workflow shared via `.claude/` (commands, hook script, project settings.json); `.claude/settings.local.json` stays gitignored.
