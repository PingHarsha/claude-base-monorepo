---
description: Verify pending or committed changes across backend and frontend — run tests, measure coverage, add missing tests, lint, security review. Auto-detects working-tree vs branch mode and which stacks are touched. Does not push.
---

You are running the project's verification pipeline. This is a read-mostly safety gate — it adds tests if missing but does not push. Surface findings to the user; do not silently move on.

**Working directories.** Backend lives at `backend/`, frontend at `frontend/`. Prefix `mvn` commands with `cd backend && `; `npm` / `ng` / `npx prettier` commands with `cd frontend && ` (or change CWD once per stack).

## 0. Detect mode and affected stacks
Run `git status --porcelain` to determine which diff to target:

- **Working-tree mode** (uncommitted changes present): review target is `git diff HEAD`. Use when iterating in the current tree.
- **Branch mode** (clean working tree on a feature branch): review target is `git diff develop...HEAD`.
- **No-op mode** (clean tree on `main` or `develop` with no diff): report "nothing to verify" and exit.

If the user passed an explicit ref as an argument, use `git diff <ref>...HEAD` instead.

Determine which stacks the diff touches:
- **Backend affected** = any file under `backend/` in the diff
- **Frontend affected** = any file under `frontend/` in the diff

Both can be true. Run gates only for stacks that changed; skip the rest. State at the top of your output: mode, diff target, and which stacks are in scope.

## 1. Tests pass
- **Backend** (if affected): from `backend/`, `mvn test`. Generates JaCoCo coverage report at `target/site/jacoco/`.
- **Frontend** (if affected): from `frontend/`, `npx ng test --watch=false --browsers=ChromeHeadless`.

If either fails, stop and report — do not continue.

## 2. Add coverage for new/changed logic
For each meaningfully changed class/component in the diff, ensure tests exist. Add focused tests where missing.

- **Backend**: use `backend/target/site/jacoco/jacoco.csv` to identify uncovered lines. Skip trivially obvious code (getters, `@SpringBootApplication` main).
- **Frontend**: rely on the diff and your judgment — there's no JaCoCo equivalent wired in yet. Cover new public methods, new behavioral branches, and template changes that have logic.

Re-run the test command for the affected stack(s) after adding tests.

## 3. Coverage threshold (backend only)
If backend was affected, from `backend/` run `mvn jacoco:check`. Threshold is 80% line coverage at bundle level. If it fails, parse `target/site/jacoco/jacoco.csv` for classes below threshold and either add more tests or surface the gap to the user.

Frontend has no enforced threshold yet — if the diff added substantial untested frontend logic, flag it in the report.

## 4. Lint
- **Backend** (if affected): from `backend/`, `mvn spotless:check`. If dirty, run `mvn spotless:apply` and re-run `mvn test`. Note in the report if formatting was auto-applied.
- **Frontend** (if affected): from `frontend/`:
  - `npm run format:check` (Prettier). If dirty, `npm run format` to auto-fix, then re-run `ng test`.
  - `npm run lint` (ESLint via @angular-eslint). For auto-fixable lint issues, `npx ng lint --fix` then re-run. Genuine lint errors that aren't auto-fixable stop the pipeline.

## 5. Security review
Review the target diff (both stacks) for:
- Injection risks (SQL, command, LDAP, expression-language, XSS in Angular templates)
- Hardcoded secrets, credentials, tokens (backend properties OR frontend env files)
- Unsafe deserialization
- Missing authn/authz checks
- Path traversal, SSRF
- Unbounded resource use (memory, file handles, threads, large client-side state)
- Sensitive data in logs (server logs AND browser console)

Substantive findings → stop and surface them, classified by severity (block / fix-before-ship / nit). Don't just note them — make the user decide.

## 6. Commit added tests / formatting fixes (branch mode only)
- **Working-tree mode**: leave added tests and auto-applied formatting unstaged for `/ship`.
- **Branch mode**: commit added tests and any auto-applied formatting on the current branch (`test:`, `chore:`, or `style:` prefix as appropriate). In a worktree-isolated background run, "the current branch" is the agent's worktree branch — that's correct; the user will merge it back.

Do not push under any circumstances.

## 7. Report
End with a short summary the user can act on:
- Mode + diff target + stacks in scope
- Per stack:
  - Tests: pass/fail counts
  - Coverage (backend): overall % + classes below threshold
  - Lint: clean / auto-applied N fixes / errors that need attention
- Security review: clean or itemized findings with severity
- Branch mode: branch name to merge back
