---
description: Pre-merge gate. Runs integration tests on the current feature branch, shows results, pauses for explicit "approve", then merges to develop and deletes the branch.
---

You are running the merge-to-develop gate. This is the explicit approval moment: integration tests run, the user sees the results, the user says "approve", and only then does the merge happen.

## 0. Preflight
Stop on any failure here — surface the issue and let the user resolve it before re-running.

- **On a feature branch?** Run `git branch --show-current`. If on `main` or `develop`, stop — this command merges INTO develop, not from it.
- **Working tree clean?** Run `git status --porcelain`. If dirty, stop — only committed state should merge.

## 1. Drift check against develop
Run `git fetch origin develop` to refresh (ignore failure if no remote/auth). Compare:

    git rev-list HEAD..develop --count

If > 0, `develop` has commits this branch doesn't have. Warn the user — integration tests against this branch will not reflect the post-merge state. Ask: rebase onto develop first, merge develop into the feature branch first, or proceed anyway? Default recommendation: rebase if the branch is small, merge if rebasing would be painful.

## 2. Run integration tests
Run `mvn test -P integration-tests`. If anything fails:
- Stop. Do not merge.
- Surface failing test names and the relevant assertion / stack trace.
- Recommendation: fix the regression on this branch, then re-run `/merge`.

## 3. Sanity-check unit tests
Run `mvn test` to confirm the default profile still passes (cheap, ~3s). If it fails (unlikely if `/verify` was run), stop and report.

## 4. Show results and pause for approval
Print:
- Branch + HEAD commit SHA
- Diff against develop: `git diff develop..HEAD --stat`
- Unit test counts (pass/fail)
- Integration test counts (pass/fail)
- Any drift warning from step 1

End with: **"Reply with `approve` to merge this branch into `develop`, or anything else to cancel."**

Wait for the user's explicit response. Only proceed on a clear `approve`. Anything else cancels the merge — leave the branch as-is.

## 5. Merge
On approval:
- `git checkout develop`
- `git merge <feature-branch>` (fast-forward when possible, merge commit otherwise)
- `git branch -d <feature-branch>`
- Show the new `develop` HEAD: `git log --oneline -3`

Do not push. The user pushes `develop` when they're ready (typically just before `/promote`).
