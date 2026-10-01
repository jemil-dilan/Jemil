# NYI Backend

Backend for **NYI — Le Voyage, Autrement**, an interurban transport platform in Cameroon.

**Modular monolith** · Hexagonal / DDD · OpenAPI contract-first · PostgreSQL + Liquibase

> Live progress tracker: [`status.md`](status.md)

## Current Status (Sep 2026)

| Phase | Status |
|-------|--------|
| Sprint 0 — Foundation | Done |
| Sprint 1 — Agency & routes | Done (Cucumber e2e green) |
| **S1.5 — Model correction + trip/hold + tripgen** | **Closed** (see [`status.md`](status.md)) |
| Sprint 2 — Seat map + hold expiry | Not started |
| Sprint 3 — Payment MoMo | Not started |
| Sprint 4+ — Ticket, counter, launch | Not started |

### Modules

```text
src/main/java/cm/nyi/
├── agency/       # Production — agencies, branches, cities, routes, schedules
├── booking/      # Trips search, seat hold, trip generation (payment not yet)
├── auth/         # JWT login/register (roles include CASHIER)
├── payment/      # Empty shell (demo purged)
├── ticket/       # Empty shell (demo purged)
├── trip/         # Empty shell (demo purged)
└── shared/       # Security, outbox, exceptions, CreatedAt, …
```

## Implemented APIs

### Agency
- Register / list / get / suspend agency
- Branches, cities, add routes
- `GET /routes/search`

### Booking (S1.5)
- `GET /trips/search?originCityId&destinationCityId&serviceDate`
- `POST /bookings` — seat hold (409 if seat taken, 10 min TTL)
- Nightly trip generation from `schedule_templates` (Africa/Douala, 14-day window)
- Partial unique index `seat_once_per_trip` + concurrency test (20 parallel → 1 win)

### Auth
- Register, login, refresh (JWT multi-role)

## Architecture

```text
cm.nyi.{context}/
├── domain/
├── application/
├── adapter/inbound/rest/
├── adapter/outbound/
└── config/
```

- Domain must not depend on Spring/JPA/REST.
- Controllers call use cases, not repositories.
- ArchUnit enforces hexagonal + module isolation.

## Stack

Java 25 · Spring Boot 4 · Gradle · PostgreSQL · Liquibase · MapStruct · OpenAPI Generator · Cucumber · Testcontainers · Spotless / Checkstyle / Jacoco

## Local Development

```bash
docker compose up -d          # Postgres, Keycloak, Sonar
./gradlew bootRun             # http://localhost:8080
```

Swagger: `http://localhost:8080/swagger-ui.html`

```bash
./gradlew test                # unit + integration
./gradlew e2eTest             # Cucumber (agency + booking)
./gradlew spotlessApply
./gradlew build
```

## Docs map

| Doc | Purpose |
|-----|---------|
| [`status.md`](status.md) | **Progress board — update this first** |
| [`docs/NYI_MVP_Specification_Document_v1.1.docx`](docs/NYI_MVP_Specification_Document_v1.1.docx) | **MVP specification — source of truth** |
| [`docs/NYI_Sprint_Plan.md`](docs/NYI_Sprint_Plan.md) | Sprint-by-sprint plan |
| [`docs/NYI_Implementation_Guideline.md`](docs/NYI_Implementation_Guideline.md) | How work is executed and verified |
| [`docs/deployment/`](docs/deployment/) | Deployment guides |
| [`docs/archive/`](docs/archive/) | Superseded pre-rebrand documents — historical record only |
| [`specs/adr/`](specs/adr/) | Architecture decision records |
| [`specs/openapi/`](specs/openapi/) | API contracts (source of truth) |
| [`specs/documentation/ERROR_CODES.md`](specs/documentation/ERROR_CODES.md) | Error code catalogue |

## Database

Migrations: `src/main/resources/db/changelog/` (`v0`…`v9`).  
`ddl-auto: validate` — schema changes go through Liquibase only.

## Evolution

1. Stay on one deployable while the product takes shape.
2. Promote each context as a full vertical slice (domain → API → tests).
3. Split services only when scaling/ownership requires it.
