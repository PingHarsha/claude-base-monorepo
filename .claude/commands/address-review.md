---
description: Fetch a PR's review feedback, present it for selection, branch off develop, and apply the user-accepted items as uncommitted edits. Use after /review (default), /ultrareview (escalation), or any human review.
---

You are running the address-review pipeline. The user has a PR with review feedback (typically from `/review`, occasionally from `/ultrareview` or a human reviewer) and wants to systematically work through it. This command does **not** commit — it applies edits to a fresh branch; the user then runs `/verify` → `/ship` → `/merge` to close the loop.

**Backend path.** `mvn` invocations below run from `backend/`. Frontend lives at `frontend/`.

## 0. Preflight
Stop on any failure here and let the user resolve before re-running.

- **gh CLI installed?** Run `gh --version`. If missing: `brew install gh && gh auth login`.
- **gh authed?** Run `gh auth status`. If not: `gh auth login`.
- **PR number?** From the invocation argument. If missing, ask.
- **On `develop`?** Run `git branch --show-current`. If on `main` or a feature branch, ask whether to switch to `develop` (the fix branch should be off `develop`).
- **Working tree clean?** Run `git status --porcelain`. If dirty, stop.

## 1. Fetch all PR feedback
Get the repo identifier first:

    gh repo view --json owner,name

Then fetch in parallel:

    gh pr view <PR#> --json number,title,headRefName,reviews,url
    gh api repos/<owner>/<name>/pulls/<PR#>/comments
    gh pr view <PR#> --comments

Parse:
- **Reviews**: review submissions, each with `state` (APPROVED / CHANGES_REQUESTED / COMMENTED) and `body`.
- **Line-anchored review comments**: per-line code review comments with `path`, `line`, `body`, `user.login`.
- **Discussion comments**: general thread comments, not tied to specific code lines.

## 2. Present findings, numbered
Group and number (IDs continuous across sections, starting at 1):

- **Section A: Changes requested** — any review with state `CHANGES_REQUESTED`. Show the review body plus all its line-anchored comments. These typically block PR merge.
- **Section B: Other line comments** — review comments not part of a `CHANGES_REQUESTED` review. Advisory.
- **Section C: Discussion comments** — general thread comments.

For each item, show: ID, author, file:line (if line-anchored), full body. Truncate only if a single item exceeds ~500 chars (then offer to expand on request).

If there are zero findings, report that and exit cleanly — no fix branch needed.

## 3. Get the user's decision
Ask: **"Which items do you want to act on? Reply with IDs (e.g., `1, 3, 5`), `A` for all of section A, `all` for everything, or `none` to abort."**

Wait for the user's response. Parse the selection. If they say `none`, exit without creating a branch.

## 4. Create the fix branch

    git checkout -b feature/pr-<PR#>-fixes develop

If a branch by that name already exists, suggest `feature/pr-<PR#>-fixes-2` and confirm with the user.

## 5. Apply the accepted changes
Iterate through the accepted items:
- Read the comment carefully.
- If the requested change is clear and self-contained, apply it via Edit/Write.
- If ambiguous (e.g., "consider edge case X" without specifying behavior, or subjective like "this is hard to read"), **stop and ask the user** how to resolve before continuing.

Track which items were applied and how (one-line summary each).

After applying all accepted items, run `mvn test -q` as a smoke check (not a full `/verify` — that's the user's next step). Note the result in the final report.

## 6. Report and hand off
Print:
- Branch created + base commit
- **Applied** items (by ID) with a one-line "what changed" summary each
- **Skipped** items (by ID), with the user's reason if given
- Smoke test: pass/fail counts

Recommend:
- Run `/verify` to add coverage and security-review the changes
- Then `/ship` (one commit covering all PR fixes)
- Then `/merge` (integration test + approve + merge to develop)
- After merging, the existing PR for `develop → main` will pick up the new commits automatically — no new PR needed

## 7. Optional: reply to skipped comments
Ask: **"Want to post replies on the skipped comments explaining why we passed?"** If yes, prompt the user for each skipped ID's reply text, then post:

    gh api -X POST repos/<owner>/<name>/pulls/<PR#>/comments/<comment_id>/replies \
      -f body="<reply text>"

This keeps the PR conversation tidy and shows the reviewer they were heard, even on items not acted on.
