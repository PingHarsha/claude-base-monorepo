# example-desk-problem

A Spring Boot 3.4 scaffold (Java 21, Maven) used as a sandbox for exercising a structured development workflow with Claude Code.

## Quick start

```bash
# Build
mvn package

# Run tests (unit only)
mvn test

# Run tests (integration only)
mvn test -P integration-tests

# Lint check
mvn spotless:check

# Run the application
mvn spring-boot:run
```

App boots on `http://localhost:8080`.

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/actuator/health` | Spring Boot Actuator health endpoint. |

_No application endpoints yet — this scaffold is in workflow-setup phase._

## Development workflow

This repo uses an opinionated 10-step workflow (`/plan-feature` → `/ship` → `/verify` → `/merge` → `/promote` → `/ultrareview` → `/address-review`) driven from Claude Code. See [CLAUDE.md](CLAUDE.md) for the full sequence, branch policy (main / develop / feature/*), and per-command details.

Coverage is enforced at 80% line coverage (bundle-level) via JaCoCo. Code style is enforced via Spotless (Google Java Format, AOSP variant).

## Repository structure

```
src/main/java/com/example/deskproblem/      — production code
src/test/java/com/example/deskproblem/      — unit tests (MockMvc, JUnit 5)
src/test/java/com/example/deskproblem/integration/  — integration tests (@Tag("integration"), real HTTP)
PLAN.md                                     — source of truth: goals, decisions, open questions, changelog
CLAUDE.md                                   — development workflow + Claude Code conventions
```
