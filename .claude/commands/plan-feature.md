---
description: Capture feature intent before coding. Updates PLAN.md's Current focus, optionally creates the feature branch. Step 0 of the feature workflow.
---

You are running the plan-feature command. The user is about to start new work and wants to document intent first, before any code. This is a reflection moment, not a commitment moment — the spec we capture is a working draft that will evolve.

## 1. Gather the feature spec
Ask the user (one question at a time, or all at once, depending on what they've already provided):

- **Feature name**: short, kebab-case. Becomes the branch name. e.g. `notes-api` → `feature/notes-api`.
- **Goal**: what's the feature for? One or two sentences. The "why" matters more than the "what."
- **Scope**: which files / packages / endpoints will it touch? Best guess is fine.
- **Success criteria**: how will we know it's done? Specific is better than aspirational.
- **Out of scope**: anything explicitly NOT included? (Helps prevent scope creep.)
- **Open questions** (optional): things you don't know yet but will need to resolve.

If the user gives a one-line description, ask follow-ups for the missing pieces — don't infer silently.

## 2. Preflight before branching
- Confirm we're on `develop`. If on `main` or a different branch, ask whether to switch (you should be branching off `develop`).
- Confirm working tree is clean. If dirty, stop — committed state should branch.

## 3. Update PLAN.md
Edit `PLAN.md`:
- **Current focus** section: replace with a short paragraph describing this feature (goal + 1-2 sentences of context). If the previous Current focus represents completed work that hasn't been logged, move it to **Decisions** with a short rationale.
- **Open questions** section: append any user-provided open questions, prefixed with the feature name in brackets, e.g. `- [notes-api] Should notes have an expiry?`.

Don't touch the Changelog yet — that's `/ship`'s job after each commit.

## 4. Show the spec, then create the branch
Print the captured spec back to the user as a numbered list so they can verify nothing was lost in translation. Ask: "Create branch `feature/<name>` from develop? (yes/no, or edit)"

On `yes`: `git checkout -b feature/<name> develop`.
On `no`: leave them where they are; the PLAN.md updates are still useful.
On edit: take the correction and re-confirm.

## 5. Report
- Branch created (or not, if user declined)
- Path forward: implement, commit incrementally via `/ship`, then `/verify` → `/merge` to land
- Link back to `PLAN.md` so they can refine the spec as they learn
