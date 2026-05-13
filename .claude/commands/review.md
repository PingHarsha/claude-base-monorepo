---
description: Review a PR and post the review as a GitHub PR review comment for transparency. Step 8 of the workflow. Replaces the older /ultrareview reference. Requires gh CLI.
---

You are running the PR review pipeline. Read the PR diff, generate a code review honoring this repo's conventions, show it to the user for approval, then post it to GitHub as a PR review (so the review trail lives on the PR, not just in this conversation).

This command is the **single-agent, in-conversation** alternative to the cloud-billed `/ultrareview`. It costs nothing beyond the current session and is fast enough to be the default review step. Use `/ultrareview` only when you specifically want a multi-agent fresh-context pass.

## 0. Preflight
Stop on any failure here and let the user resolve before re-running.

- **gh CLI installed?** Run `gh --version`. If missing: `brew install gh && gh auth login`.
- **gh authed?** Run `gh auth status`. If not: `gh auth login`.
- **PR number?** Take it from the invocation argument. If missing, run `gh pr list` and ask the user which to review.

## 1. Gather PR context
Run in parallel:

    gh pr view <PR#> --json number,title,body,baseRefName,headRefName,additions,deletions,changedFiles,url
    gh pr diff <PR#>

If the diff exceeds ~25k tokens, re-fetch it excluding generated/lock files to keep the review focused:

    git fetch origin
    git diff origin/<base>..origin/<head> -- . \
      ':(exclude)*package-lock.json' \
      ':(exclude)*.lock' \
      ':(exclude)**/dist/**'

Note in the review body which paths were excluded from the read.

## 2. Read the conventions
Skim [CLAUDE.md](../../CLAUDE.md), especially **Code style** and the per-stack build/test sections, so the review can cite project conventions when relevant rather than imposing generic preferences. For backend changes, also note the OpenAPI annotation requirement and the JaCoCo 80% line coverage gate.

## 3. Generate the review
Structure the review as Markdown with these sections (omit any that yield no content):

- **Overview** — 1–3 sentences on what the PR does.
- **Strengths** — short list of things done well. Calibrates the review and acknowledges good calls. Skip if it'd be filler.
- **Suggestions** — actionable improvements. Each item leads with the *what*, then a one-line *why*. Prefer concrete edits over vague advice. Cite file:line where applicable.
- **Risks** — anything that could break or surprise. Label severity inline (e.g., "blocking", "latent").
- **Verdict** — one of: `Ship it`, `Ship after addressing <N> blockers`, `Block — see Risks`. Be honest; this is the signal the merger reads first.

Review lens (apply, don't recite):
- Correctness, project-convention fit (Code style section of CLAUDE.md), performance implications, test coverage adequacy, security considerations.
- For backend endpoint changes: confirm the `@Tag` / `@Operation` / `@ApiResponse` / `@Parameter` / `@Schema` annotations are present.
- For frontend changes: confirm SCSS uses shared tokens from `_variables.scss` when adding values used in more than one place.

Keep it tight. A reviewer with 5 minutes should be able to act on it.

## 4. Show, then ask
Print the full review to the conversation, then ask:

**"`approve` to post this review on the PR, `edit` to revise, or `skip` to leave it in-conversation only."**

On `edit`: take the user's revision, re-display, re-confirm.
On `skip`: stop here. The review lives in the conversation transcript only.

## 5. Post to GitHub
On `approve`, post as a non-blocking review comment (no approve/request-changes — this is advisory feedback, not gating):

    gh pr review <PR#> --comment --body "$(cat <<'EOF'
    <the approved review markdown>
    EOF
    )"

Then print the PR URL so the user can confirm it landed.

If the user explicitly wants the review to *block* the PR (review state `CHANGES_REQUESTED`), use `--request-changes` instead of `--comment`. Ask before doing so — most reviews from this command should be advisory.

## 6. Report
Print:
- PR URL
- Verdict line repeated
- Recommend: `/address-review <PR#>` if there are actionable suggestions; otherwise the user can merge via the GitHub UI (workflow step 10).
