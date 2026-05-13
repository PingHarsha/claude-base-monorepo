# Plan

> Source of truth for current goals, decisions, and open questions.
> Updated by `/ship` and by hand. Newest entries at the top of each section.

## Current focus
Phase 3 of multi-stack expansion: Angular 19 scaffold in `frontend/`. Standalone components, no router, SCSS with a shared design-tokens partial (`src/styles/_variables.scss`) wired into `stylePreprocessorOptions.includePaths` so components reuse tokens via `@use 'variables' as v;`. Hello-world main page. Phase 4 wires multi-stack workflow commands and CI (Prettier + ng lint for frontend).

## Decisions
_Append-only log of meaningful technical decisions and the reasoning behind them._

- _(none yet — workflow tooling is the only completed work so far)_

## Open questions
_Things we haven't resolved. Move resolved ones into Decisions._

- _(none yet)_

## Changelog
_Short bullet per `/ship`, newest first. Format: `YYYY-MM-DD — <summary>`_

- 2026-05-13 — Established v2 workflow tooling: slash commands (`/plan-feature`, `/ship`, `/verify`, `/merge`, `/integration-verify`, `/promote`, `/address-review`, `/quickfix`), Spotless lint (Google Java Format AOSP), JaCoCo coverage check at 80%, `integration-tests` Maven profile, README.md. Workflow shared via `.claude/` (commands, hook script, project settings.json); `.claude/settings.local.json` stays gitignored.
