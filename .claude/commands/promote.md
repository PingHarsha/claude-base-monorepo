---
description: Create a PR to merge develop into main. Step 7 of the v2 workflow. Reviews README freshness first, optionally commits an update. Requires gh CLI.
---

You are running the promotion workflow — creating a pull request to merge `develop` into `main`. Before pushing/creating the PR, you also review whether the README needs an update given what's accumulated on `develop` since the last main merge. This does not auto-merge; the user reviews the PR and merges in GitHub.

## 0. Preflight checks
Stop on any failure here — surface the issue and let the user resolve it before re-running.

- **gh CLI installed?** Run `gh --version`. If missing, stop with: "Install gh: `brew install gh && gh auth login`."
- **gh authenticated?** Run `gh auth status`. If logged out, stop with `gh auth login` instructions.
- **On `develop`?** Run `git branch --show-current`. If not `develop`, ask the user whether to switch. Don't promote from a feature or `main`.
- **Working tree clean?** Run `git status --porcelain`. If anything is uncommitted, stop — only committed state should promote.
- **develop ahead of main?** Run `git rev-list main..develop --count`. If 0, report "develop is equal to main — nothing to promote" and exit.

## 1. README freshness check
Inspect the diff `git diff main..HEAD` for changes that might make the README stale. Triggers worth checking:

- **New or removed endpoints**: new `@RestController` classes; new `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` methods; renames; deletions
- **Endpoint behavior changes**: new required params, new validation rules, new response fields
- **New build commands**: new Maven plugins in `pom.xml` that users would invoke (e.g., we added Spotless → `mvn spotless:check`)
- **New top-level files/directories** users should know about (e.g., a new `docs/` directory, a new config file)
- **Dependency or version changes** users running locally might notice (rare)

Read `README.md`. For each triggered item, decide:
- README **already covers** it → no action needed
- README is **stale or missing** the info → propose a specific edit

If no updates are warranted, say so and proceed to step 2.

If updates are warranted, present them to the user as a list of proposed edits with the diff context that motivated each one. Ask: **"`approve` to apply and commit, `edit` to revise, or `skip` to leave README as-is and proceed."**

On `approve`:
- Apply the proposed edits to `README.md` using Edit.
- Stage and commit on `develop`:
  ```
  git add README.md
  git commit -m "docs: refresh README for develop→main promotion"
  ```
  Use a more specific message if a single change dominates (e.g., `docs: document /api/notes endpoint`).

On `edit`: take the user's correction, re-apply, re-confirm.
On `skip`: leave README as-is. Note in the PR body (step 3) that the README was reviewed and judged not to need changes.

## 2. Push develop to origin
Run `git push origin develop` (add `-u` if origin/develop doesn't yet track). If the push fails or wants `--force`, stop and report — do not force without explicit user approval.

## 3. Generate PR title and body
Title (under 70 chars):
- If `git log main..develop` has a single dominant theme, use that, e.g. `Promote: add /api/echo and v2 workflow tooling`.
- Otherwise: `Promote develop → main (<N> commits)`.

Body — compose from:
- **Highlights** — pull the most recent entries from `PLAN.md` under `## Changelog`.
- **Commits** — output of `git log main..develop --pretty=format:"- %s" --no-merges`.
- **README review** — note from step 1: either "Updated in commit <SHA>" or "Reviewed; no changes needed."
- **Verification** — note that `/verify` ran clean on each feature branch and `/integration-verify` passed on develop. If either was skipped, say so.
- **Open questions** — if `PLAN.md` has anything under `## Open questions` relevant to this promotion, surface them as items for the reviewer's attention.

## 4. Create the PR
Use a heredoc for the body to preserve formatting:

    gh pr create --base main --head develop \
      --title "<title>" \
      --body "$(cat <<'EOF'
    ## Highlights
    ...
    ## Commits
    ...
    ## README review
    ...
    ## Verification
    ...
    EOF
    )"

## 5. Report
- Print the PR URL returned by `gh pr create`.
- Recommend the next step: `/ultrareview <PR#>` for a fresh-context, multi-agent review.
- Remind the user: final merge to `main` is done by them in the GitHub UI after review (workflow step 10).
