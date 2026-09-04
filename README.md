# JEMIL Backend

Backend for an interurban transport platform in Cameroon.

**Modular monolith** · Hexagonal / DDD · OpenAPI contract-first · PostgreSQL + Liquibase

> Live progress tracker: [`docs/STATUS.md`](docs/STATUS.md)

## Current Status (Sep 2026)

| Phase | Status |
|-------|--------|
| Sprint 0 — Foundation | Done |
| Sprint 1 — Agency & routes | Done (Cucumber e2e green) |
| **S1.5 — Model correction + trip/booking hold APIs** | **In progress / nearly closed** |
| Sprint 2 — Payment MoMo | Not started |
| Sprint 3+ — Ticket, counter, launch | Not started |

### Modules

```text
src/main/java/cm/jemil/
├── agency/       # Production — agencies, branches, cities, routes, schedules
├── booking/      # Inventory + trip search + seat hold (payment still scaffold)
├── auth/         # JWT login/register (roles include CASHIER)
├── payment/      # Demo scaffold only
├── ticket/       # Demo scaffold only
├── trip/         # Demo scaffold only
└── shared/       # Security, outbox, exceptions, CreatedAt, …
```

## Implemented APIs

### Agency
- Register / list / get / suspend agency
- Branches, cities, add routes
- `GET /routes/search`

### Booking (S1.5)
- `GET /trips/search?originCityId&destinationCityId&serviceDate`
- `POST /bookings` — seat hold (409 if seat taken)
- Partial unique index `seat_once_per_trip` + concurrency test (20 parallel → 1 win)

### Auth
- Register, login, refresh (JWT multi-role)

## Architecture

```text
cm.jemil.{context}/
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
| [`docs/STATUS.md`](docs/STATUS.md) | **Progress board — update this first** |
| [`docs/JEMIL_MVP_Build_Spec.md`](docs/JEMIL_MVP_Build_Spec.md) | MVP product / schema / payment rules |
| [`docs/JEMIL_Backend_Gap_Analysis.md`](docs/JEMIL_Backend_Gap_Analysis.md) | Defects D1–D7 + revised sprint plan (S1.5…) |
| [`docs/JEMIL_Use_Cases_Acceptance_Criteria.md`](docs/JEMIL_Use_Cases_Acceptance_Criteria.md) | P0/P1/P2 use cases + AC |
| [`docs/JEMIL_UC_Detail_Part1_Passenger_System.md`](docs/JEMIL_UC_Detail_Part1_Passenger_System.md) | Passenger/system UC detail |
| [`docs/JEMIL_UC_Detail_Part2_Staff_Apps.md`](docs/JEMIL_UC_Detail_Part2_Staff_Apps.md) | Counter/controller UC detail |
| [`docs/sprints/`](docs/sprints/) | Sprint retrospectives |
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
