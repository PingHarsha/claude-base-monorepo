# example-desk-problem

A monorepo sandbox for exercising a structured development workflow with Claude Code. Spring Boot 3.4 backend (Java 21, Maven); Angular 19 frontend (coming in phase 3).

## Repository structure

```
backend/                       — Spring Boot 3.4 / Java 21 / Maven application
  pom.xml
  src/main/java/com/example/deskproblem/
  src/test/java/com/example/deskproblem/             — unit tests (MockMvc)
  src/test/java/com/example/deskproblem/integration/ — integration tests (@Tag("integration"), real HTTP)
frontend/                      — Angular 19 application (placeholder until phase 3)
.github/workflows/             — GitHub Actions: PR checks + integration tests on main
.claude/                       — slash commands, hooks, project Claude Code settings
PLAN.md                        — source of truth: goals, decisions, open questions, changelog
CLAUDE.md                      — development workflow + per-stack conventions
```

## Quick start

Backend commands run from `backend/`:

```bash
cd backend

# Build
mvn package

# Run unit tests
mvn test

# Run integration tests
mvn test -P integration-tests

# Lint check (Spotless / Google Java Format AOSP)
mvn spotless:check

# Unit tests + coverage threshold (JaCoCo, 80% line coverage)
mvn verify

# Run the application
mvn spring-boot:run
```

Backend boots on `http://localhost:8080`.

Frontend commands run from `frontend/`:

```bash
cd frontend
npm install                # first time only
ng serve                   # dev server at http://localhost:4200 (hot reload)
ng build                   # production build (output to frontend/dist/frontend/)
ng test                    # unit tests via Karma + Jasmine
npm run lint               # ESLint via @angular-eslint
npm run format:check       # Prettier (auto-fix: npm run format)
```

Frontend uses SCSS. Shared design tokens (colors, spacing, typography) live in `frontend/src/styles/_variables.scss`; components reuse them via `@use 'variables' as v;`. See [CLAUDE.md](CLAUDE.md) for the shared-styles convention.

## Local database

The backend connects to Postgres. Bring it up locally with Docker:

```bash
docker compose up -d
```

This starts Postgres 16 on `localhost:5432` with database `example_desk`, user `example_desk`, password `example_desk` — matching the defaults in `backend/src/main/resources/application.properties`. Stop with `docker compose down`; reset (wipe data) with `docker compose down -v`. To point the backend at a different Postgres, set `DB_URL`, `DB_USER`, `DB_PASSWORD` env vars before starting.

Tests use Testcontainers to spin up Postgres on the fly, so `docker compose up` is not required to run `mvn verify` — only the Docker daemon needs to be running.

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/actuator/health` | Spring Boot Actuator health endpoint. |

_No application endpoints yet — this scaffold is in workflow-setup phase._

## Development workflow

This repo uses an opinionated 10-step workflow (`/plan-feature` → `/ship` → `/verify` → `/merge` → `/promote` → `/ultrareview` → `/address-review`) driven from Claude Code. See [CLAUDE.md](CLAUDE.md) for the full sequence, branch policy (main / develop / feature/*), and per-command details.

Backend quality gates: Spotless (formatting), JaCoCo (80% line coverage), security review on diff. Frontend quality gates: Prettier (formatting), `ng lint` / ESLint (style + quality), `ng test` (unit tests). Both stacks run in parallel via GitHub Actions on every PR to `develop` or `main`.
