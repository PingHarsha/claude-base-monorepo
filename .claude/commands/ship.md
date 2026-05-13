---
description: Update PLAN.md, propose a commit message, pause for approval, then commit. Assumes /verify already passed.
---

You are running the project's commit pipeline. Assumes `/verify` has already run cleanly — if the user hasn't run it recently, remind them and ask whether to proceed anyway.

## 1. Branch check
If currently on `main` or `develop`, ask the user whether to create a feature branch (`feature/<short-kebab>`) before committing. Do not commit to either branch silently — both are integration branches.

## 2. Sanity check on test state
Quickly run `mvn test -q` to confirm the tree still passes (in case edits happened after `/verify`). If it fails, stop — send them back to `/verify`.

## 3. Update PLAN.md
Edit `PLAN.md` to reflect what's shipping:
- Move resolved items out of **Open questions** into **Decisions** with the rationale.
- Append a **Changelog** entry: `YYYY-MM-DD — <one-line summary>` (use today's date).
- Update **Current focus** if scope shifted.

## 4. Propose commit, then pause
Stage the changes (`git add` by name — never `git add -A`). Show the user:
- A short summary of what's staged (files + lines added/removed)
- A proposed commit message in conventional-commit style (e.g. `feat(orders): add idempotency key validation`)

**Wait for explicit user confirmation** before running `git commit`. If the user wants edits to the message, apply them and re-confirm.

## 5. Commit
On approval, run `git commit` with the agreed message via heredoc. Do not push unless the user asks.
