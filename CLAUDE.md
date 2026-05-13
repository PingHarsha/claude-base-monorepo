# example-desk-problem

Spring Boot 3.4 / Java 21 / Maven scaffold. No Maven wrapper — use system `mvn`.

## Build & test
- Unit tests: `mvn test`
- Unit tests + coverage check: `mvn verify` (JaCoCo enforces 80% line coverage at bundle level; `Application.class` is excluded)
- Integration tests: `mvn test -P integration-tests`
- Lint check: `mvn spotless:check`
- Lint apply: `mvn spotless:apply`
- Build: `mvn package`
- Run: `mvn spring-boot:run`

Coverage report after `mvn test` is at `target/site/jacoco/index.html` (or `jacoco.csv` for parsing).

Source layout: `src/main/java/com/example/deskproblem/`, tests mirror under `src/test/java/...`.
Integration tests live in `src/test/java/com/example/deskproblem/integration/` and are tagged `@Tag("integration")`. They use `@SpringBootTest(webEnvironment=RANDOM_PORT)` + `TestRestTemplate` to hit endpoints over real HTTP. The `integration` tag is excluded from default `mvn test` runs (Surefire `<excludedGroups>` config in pom.xml) and is the sole group included by the `integration-tests` Maven profile.

## Code style

Mechanical style (formatting, imports, whitespace) is enforced by Spotless. The points below cover the substance layer — they're lenses, not rules. When any of them fights with readability, **readability wins**.

- **Readability first.** Three similar lines beat a clever abstraction that hides intent. Code is read more often than it's written; optimize for the reader.
- **Testable by design.** Prefer constructor injection over field injection (`@Autowired` on fields makes test setup harder). Keep side effects at the edges; make core logic pure where it can be. If a method is awkward to unit-test, that's usually a design smell, not a test smell.
- **DRY, pragmatically.** Rule of three: don't extract a shared utility on the second duplication. Wait until the pattern stabilizes. A wrong abstraction costs more than a duplicated block.
- **SOLID, pragmatically.** Use the principles as lenses, not commandments:
  - *Single responsibility* — classes/methods small enough to describe in one sentence.
  - *Open/closed, Liskov, Interface segregation* — useful when polymorphism is real; **don't create interfaces for single-implementation classes** just because.
  - *Dependency inversion* — depend on what you *use*, not on what something *is*; but again, only when polymorphism is real.
- **No premature abstraction.** No "future-proofing." If a method has one caller, inline it. If a class has no real polymorphism, make it concrete.
- **Comments explain *why*, not *what*.** Well-named identifiers cover the *what*. Comments earn their keep on non-obvious constraints, hidden invariants, or surprising workarounds.
- **Tests are code too.** Same readability bar. A test you can't grok in 10 seconds is a bad test. Avoid clever DSLs and over-engineered fixtures.

## Branch policy
- `main` = release/stable. Tagged for releases. Don't commit directly.
- `develop` = integration branch. Features land here. Not guaranteed bug-free.
- `feature/<short-kebab>` = work happens here. Branched off `develop`, merged back via `/ship`.
- After merging a feature branch, delete it without asking.

## Workflow (10 steps)

1. All features branch off `develop`.
2. New work → run **`/plan-feature`** to capture intent (goal, scope, success criteria, out-of-scope, open questions) into `PLAN.md` and create the `feature/<kebab>` branch from `develop`. Confirm the name with the user.
3. Make changes on the feature branch and commit incrementally via **`/ship`**. `/ship` updates `PLAN.md`'s Changelog and pauses for commit-message approval.
4. Run **`/verify`** on the feature branch. It runs `mvn test`, adds missing coverage, runs Spotless lint (auto-applies if dirty), and security-reviews the diff. Reports findings; the user resolves anything blocking.
5. Run **`/merge`** on the feature branch. It runs the integration test suite (`mvn test -P integration-tests`), shows results + the diff against develop, and **pauses for explicit `approve`**. On approval, it merges to `develop`, deletes the feature branch, and pushes `develop` to origin (so GitHub Actions can pick it up). (For ad-hoc integration runs not tied to a merge, `/integration-verify` runs the same test suite without the merge step.)
6. Bugs found later → new branch off `develop`, fix, `/verify`, `/merge`. Same loop.
7. When `develop` is ready to release, run **`/promote`**. It (a) reviews `git diff main..HEAD` for README-worthy changes and proposes a README update on `develop` if anything is stale, (b) creates a `develop → main` PR with title and body derived from `git log main..develop` and the PLAN.md changelog. Requires `gh` CLI installed and authenticated. Consider running `/integration-verify` on `develop` first as a final smoke check.
8. Run **`/ultrareview <PR#>`** for a fresh-context multi-agent PR review (user-triggered; Claude cannot launch this directly). Findings post as PR comments.
9. User reviews the PR + the review's findings. To work through the findings systematically, invoke **`/address-review <PR#>`** — it fetches comments via `gh`, lists them numbered, lets you pick which to act on, branches off `develop`, and applies the accepted changes as uncommitted edits. From there you run `/verify` → `/ship` → `/merge` per the bug-fix loop (step 6). After merge to `develop`, the existing `develop → main` PR picks up the new commits automatically.
10. User merges the PR into `main` in the GitHub UI.

### Per-step command reference
| Step | Command | Location |
|---|---|---|
| 2 (feature kickoff) | `/plan-feature` | [.claude/commands/plan-feature.md](.claude/commands/plan-feature.md) |
| 3 (each commit) | `/ship` | [.claude/commands/ship.md](.claude/commands/ship.md) |
| 4 (pre-merge gate) | `/verify` | [.claude/commands/verify.md](.claude/commands/verify.md) |
| 5 (integration + merge) | `/merge` | [.claude/commands/merge.md](.claude/commands/merge.md) |
| 5/7 (ad-hoc integration run) | `/integration-verify` | [.claude/commands/integration-verify.md](.claude/commands/integration-verify.md) |
| 7 (PR develop→main) | `/promote` | [.claude/commands/promote.md](.claude/commands/promote.md) |
| 8 (fresh-context PR review) | `/ultrareview <PR#>` | built-in Claude Code skill (user-triggered) |
| 9 (apply PR review fixes) | `/address-review <PR#>` | [.claude/commands/address-review.md](.claude/commands/address-review.md) |
| — (trivial changes only) | `/quickfix` | [.claude/commands/quickfix.md](.claude/commands/quickfix.md) |

### Lightweight path for trivial changes
For genuinely trivial work (typos, doc tweaks, comment-only edits, one-line obvious fixes), use **`/quickfix`** instead of the full pipeline. It runs unit tests + Spotless, proposes a commit, pauses for approval, commits, and (on a feature branch) optionally merges to `develop`. Skips integration tests and the coverage threshold. Use sparingly — when in doubt, take the full path.

## Backgrounded verify
Because `/verify` supports branch mode (clean tree on a feature branch → diffs against `develop`), it can run as a background agent in an isolated worktree against a committed feature branch while the user continues on a different feature in the foreground. Pattern: user `/ship`s feature A → "spawn a background agent in an isolated worktree to run /verify on feature/A" → user moves on to feature B → agent reports back with a branch to merge.

## Plan document
[PLAN.md](PLAN.md) at the repo root is the source of truth for current goals, decisions, open questions, and the per-ship changelog. Keep it up to date — `/ship` updates it, `/promote` reads from it for the PR body.

## One-time setup
- **GitHub branch protection on `main`** — enforces the no-direct-commits rule at the platform level. In GitHub: **Settings → Branches → Add branch protection rule** for branch name pattern `main`. Recommended settings:
  - Require a pull request before merging (set required approvals to 0 if you're solo, otherwise 1+)
  - Require status checks to pass before merging — add `PR checks / checks` (from [.github/workflows/pr-checks.yml](.github/workflows/pr-checks.yml)) once it's run at least once. `Integration tests` (from [.github/workflows/integration-tests.yml](.github/workflows/integration-tests.yml)) fires on push to `main`, so it's not a PR gate but is visible per-commit on the main timeline.
  - Require linear history (forces fast-forward / rebase; prevents merge commits)
  - Do not allow administrators to bypass the above
- **`gh` CLI** — install via `brew install gh && gh auth login`. Required for `/promote` and `/address-review`.

## Automated feedback
A `Stop` hook runs `mvn test -q` after each of my turns when there are uncommitted `.java` changes (see `.claude/hooks/test-on-java-changes.sh`). Failing tests surface automatically; you don't need to ask me to run them.
