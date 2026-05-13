# Plan

> Source of truth for current goals, decisions, and open questions.
> Updated by `/ship` and by hand. Newest entries at the top of each section.

## Current focus
Learning the `/verify` and `/ship` workflow by building small REST API endpoints in this Spring Boot scaffold.

## Decisions
_Append-only log of meaningful technical decisions and the reasoning behind them._

- 2026-05-13 — Use manual input validation in controllers rather than adding `spring-boot-starter-validation`. Reason: keep dependencies minimal until validation logic outgrows simple if-checks.
- 2026-05-13 — 200-character cap on `/api/echo` `message` param. Reason: arbitrary safety bound to prevent unbounded resource use; revisit if a real consumer needs longer values.

## Open questions
_Things we haven't resolved. Move resolved ones into Decisions._

- Should utility endpoints (echo, future health/info) sit behind auth as the API grows? `/api/echo` is currently unauthenticated by design.
- Standardize an error response envelope shape (e.g., consistent `{error: {code, message}}`), or continue with per-endpoint `{"error": "..."}` style?

## Changelog
_Short bullet per `/ship`, newest first. Format: `YYYY-MM-DD — <summary>`_

- 2026-05-13 — Added `GET /api/echo` endpoint with input validation (required, max 200 chars) and 5-test coverage.
