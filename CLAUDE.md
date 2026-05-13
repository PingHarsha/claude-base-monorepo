# example-desk-problem

Spring Boot 3.4 / Java 21 / Maven scaffold. No Maven wrapper — use system `mvn`.

## Build & test
- Test: `mvn test`
- Build: `mvn package`
- Run: `mvn spring-boot:run`

Source layout: `src/main/java/com/example/deskproblem/`, tests mirror under `src/test/java/...`.

## Branch policy
- `main` is the release/stable branch. Don't commit directly to it.
- `develop` is the integration branch — completed features merge here first; releases promote `develop` → `main`.
- New feature → new branch off `develop`, named `feature/<short-kebab>`. Confirm the name with the user before creating.
- If the user starts describing a feature while on `main` or `develop`, ask whether to branch first.

## Ship workflow (two-phase)
The workflow is split into a verification gate and a commit step, so the user can decide whether to ship after seeing what verification surfaced.

1. **`/verify`** ([.claude/commands/verify.md](.claude/commands/verify.md)) — runs `mvn test`, adds missing coverage, security-reviews the diff. Auto-detects: working-tree diff when there are uncommitted changes, branch-vs-`main` diff when the tree is clean. Optional ref argument overrides detection. Reports findings; does not push.
2. **`/ship`** ([.claude/commands/ship.md](.claude/commands/ship.md)) — assumes `/verify` already passed. Updates `PLAN.md`, proposes a commit message, pauses for confirmation, then commits.

If the user invokes `/ship` without running `/verify` first, remind them but let them choose to proceed.

### Backgrounded verify
Because `/verify` supports branch mode, it can be run as a background agent in an isolated worktree against a committed feature branch while the user continues on a different feature in the foreground. Pattern: user commits feature A → "spawn a background agent in an isolated worktree to run /verify on feature/A" → user moves on to feature B → agent reports back with a branch to merge.

`PLAN.md` at the repo root is the source of truth for current goals, decisions, and open questions. Keep it up to date — don't let it rot.

## Automated feedback
A `Stop` hook runs `mvn test -q` after each of my turns. Failing tests will be surfaced automatically; you don't need to ask me to run them.
