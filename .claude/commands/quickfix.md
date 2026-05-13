---
description: Lightweight commit-and-merge for trivial changes across backend or frontend (typos, doc tweaks, one-line obvious fixes). Skips the full /verify and /merge pipeline. Use ONLY for genuinely trivial changes.
---

You are running the quickfix pipeline. This bypasses the full workflow for trivial changes that don't warrant `/verify` + `/merge`. Use sparingly — when in doubt, use the full pipeline.

**Working directories.** Backend at `backend/`, frontend at `frontend/`.

**Qualifies as quickfix:**
- Typo fixes
- Doc tweaks (`README.md`, `CLAUDE.md`, `PLAN.md`, comments)
- Comment-only changes
- One-line bug fixes obvious enough that integration tests wouldn't surface new info
- Whitespace / formatting fixes that don't change behavior

**Does NOT qualify:**
- Anything touching production logic, validation, error handling
- Anything that adds or removes a public method/endpoint/component
- Anything that changes a dependency or build config (`pom.xml`, `package.json`, `angular.json`)
- Anything where you'd want a reviewer's second pair of eyes

## 0. Preflight
- Confirm `git status --porcelain` shows changes. If empty, exit — nothing to fix.
- Confirm NOT on `main`. If on `main`, stop — even quickfixes should not commit to main.
- **Triviality check:** review the diff. If anything changes behavior in a non-obvious way, stop and ask: "This doesn't look trivial — run the full `/verify` + `/merge` pipeline instead?"
- Determine affected stacks: **backend** (files under `backend/`), **frontend** (files under `frontend/`), or both.

## 1. Quick gates (per affected stack)
Skip stacks not touched by the diff.

- **Backend**: from `backend/`, `mvn test` then `mvn spotless:check` (run `mvn spotless:apply` if dirty, then re-test).
- **Frontend**: from `frontend/`, `npm run format:check` (run `npm run format` if dirty) then `npm run lint`. Skip `ng test` — Karma startup is too heavy for the quickfix pipeline; full test runs are `/verify`'s job.

Do NOT run integration tests or JaCoCo check — those belong to the full pipeline.

## 2. Propose commit
Show:
- `git diff` short summary (file list + stat)
- Proposed commit message in conventional-commit style. Prefix should be `fix:`, `docs:`, `chore:`, or `style:` — never `feat:` (a quickfix that's a feature should go through the full pipeline).

Wait for user approval (`approve` to commit, or supply an edited message).

## 3. Commit
- `git add` the relevant files by name (not `-A`)
- `git commit` with the agreed message

## 4. Merge to develop and push (if on a feature branch)
Check current branch.

- **On `develop` already**: commit stays here. Push it: `git push origin develop`. If push fails, surface the error but don't roll back the commit. Report and exit.
- **On a feature branch**: ask "Merge to develop and delete this branch? (yes/no)". On yes:
  - `git checkout develop && git merge <branch> && git branch -d <branch>`
  - `git push origin develop`
  - If push fails, surface the error but don't roll back the local merge.

## 5. Report
- Commit SHA + message
- Branch state (committed-and-stayed, merged-and-deleted, or no-op)
- Reminder: integration tests, JaCoCo coverage check, and full frontend test suite were NOT run. If you discover something broke later, fix via the full workflow.
