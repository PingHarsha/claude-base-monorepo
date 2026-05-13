---
description: Verify pending or committed changes — run tests, measure coverage, add missing tests, lint, security review. Auto-detects working-tree vs branch mode. Does not push.
---

You are running the project's verification pipeline. This is a read-mostly safety gate — it adds tests if missing but does not push. Surface findings to the user; do not silently move on.

## 0. Detect mode
Run `git status --porcelain` to determine which diff to target:

- **Working-tree mode** (uncommitted changes present): the review target is `git diff HEAD` (staged + unstaged). Use this when the user is iterating on a feature in their current tree.
- **Branch mode** (clean working tree on a feature branch): the review target is `git diff develop...HEAD` — i.e., everything this feature branch has added vs `develop`. Use this when verifying a completed feature, including when this command is running inside an isolated worktree as a background agent.
- **No-op mode** (clean tree on `main` or `develop` with no diff): report "nothing to verify" and exit.

If the user passed an explicit ref as an argument, use `git diff <ref>...HEAD` instead. Treat the argument as authoritative.

State at the top of your output which mode you're in and what your diff target is, so the user can correct you if you guessed wrong.

## 1. Verify tests pass
Run `mvn test`. If anything fails, stop and report — do not continue.

This also generates a JaCoCo coverage report at `target/site/jacoco/index.html` and CSV at `target/site/jacoco/jacoco.csv`, both used in step 3.

## 2. Add coverage for new/changed logic
For each meaningfully changed class under `src/main/java/` in the target diff, check whether `src/test/java/` has corresponding coverage. Use the JaCoCo report (`target/site/jacoco/jacoco.csv`) to identify uncovered lines or branches in the changed classes — that's the authoritative source, not your eyeballing of the code.

Add focused unit tests where missing. Skip trivially obvious code (pure getters, the `@SpringBootApplication` main, etc. — `Application.class` is already excluded from JaCoCo). Re-run `mvn test` after adding tests so JaCoCo's data file is fresh.

## 3. Coverage threshold
Run `mvn jacoco:check`. The threshold is 80% line coverage (bundle-level), configured in `pom.xml`.

If it fails, parse `target/site/jacoco/jacoco.csv` for the classes below threshold and either:
- Add more focused tests until it passes
- Stop and surface to the user *why* the gap is hard to close (e.g., a class is unreachable from the test surface) and let them decide whether to lower the threshold for now or invest in testability changes

## 4. Lint check (Spotless)
Run `mvn spotless:check`. If it reports formatting issues, run `mvn spotless:apply` to auto-fix, then re-run `mvn test` to confirm the reformatted code still passes. Note in the final report that formatting was auto-applied.

If `mvn spotless:apply` itself fails for non-formatting reasons (compile errors, malformed Java, etc.), stop and surface the error — it needs human attention.

## 5. Security review
Review the target diff for:
- Injection risks (SQL, command, LDAP, expression-language)
- Hardcoded secrets, credentials, or tokens
- Unsafe deserialization
- Missing authn/authz checks
- Path traversal, SSRF
- Unbounded resource use (memory, file handles, threads)
- Sensitive data in logs

Substantive findings → stop and surface them, classified by severity (block / fix-before-ship / nit). Don't just note them — make the user decide before they move to `/ship`.

## 6. Commit added tests / formatting fixes (branch mode only)
- **Working-tree mode**: leave added tests and `spotless:apply` changes unstaged. They'll be committed by `/ship` along with the feature work.
- **Branch mode**: commit added tests and any auto-applied formatting on the current branch with message `test: add coverage for <feature>` (or `chore: apply spotless formatting` if only formatting changed). In a worktree-isolated background run, "the current branch" is the agent's worktree branch — that's correct; the user will merge it back.

Do not push under any circumstances.

## 7. Report
End with a short summary the user can act on:
- Mode + diff target used
- Tests: pass/fail counts
- Coverage: overall % + any classes below threshold (with file:line ranges if available)
- Coverage added: list of new test files/methods, or "none needed"
- Lint: clean, or "auto-applied N formatting fixes"
- Security review: clean, or itemized findings with severity
- If branch mode: the branch name to merge back
