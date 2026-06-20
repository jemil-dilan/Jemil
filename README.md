# JEMIL Backend

Backend for an interurban transport platform in Cameroon.

The current implementation is a **single Spring Boot application** organized as a
**modular monolith**. The code keeps bounded contexts separated by package so the
project can grow in one deployable unit first, and only split into services later
if operational needs justify it.

## Current Shape

```text
src/main/java/cm/jemil/
├── agency/       # Implemented reference module
├── booking/      # Demo/scaffold
├── payment/      # Demo/scaffold
├── ticket/       # Demo/scaffold
├── auth/         # Demo/scaffold
├── trip/         # Demo/scaffold
└── shared/       # Cross-cutting primitives and infrastructure
```

The `agency` package is the production-quality reference slice. The other
contexts still contain generated/demo code and should be treated as placeholders
until their real domain model is implemented.

## Architecture

JEMIL uses hexagonal architecture inside each bounded context:

```text
cm.jemil.{context}/
├── domain/                 # Business model, value objects, events, ports
├── application/            # Use case orchestration
├── adapter/
│   ├── inbound/rest/       # REST controllers and DTO mapping
│   └── outbound/           # Persistence, messaging, external systems
└── config/                 # Context-specific wiring
```

Important rules:

- Domain code must not depend on Spring, JPA, REST DTOs, or generated API code.
- Controllers call use-case interfaces, not persistence adapters.
- JPA entities are persistence models, not domain aggregates.
- Cross-context dependencies should go through `shared` primitives or events,
  not direct imports from sibling contexts.

These boundaries are partially enforced with ArchUnit tests in
`src/test/java/cm/jemil/architecture`.

## Implemented Functionality

### Agency

- Register an agency.
- List agencies, optionally filtered by city.
- Retrieve an agency by id.
- Add routes with price and total seat count.
- Persist agencies, routes, schedules, and outbox events with JPA.
- Publish domain events through an outbox table.

### Shared Infrastructure

- Spring Security resource-server setup.
- PostgreSQL + Liquibase migrations.
- Outbox persistence and scheduled publication.
- OpenAPI-generated inbound contracts and DTOs.
- Common value objects and pagination helpers.

## Technology Stack

- Java toolchain 25, compiling with `--release 21`.
- Spring Boot 4.
- Gradle Kotlin DSL.
- PostgreSQL.
- Liquibase.
- Spring Data JPA.
- Spring Security OAuth2 Resource Server.
- MapStruct.
- OpenAPI Generator.
- JUnit 5, AssertJ, Mockito, ArchUnit, Testcontainers.
- Spotless, Checkstyle, Jacoco, Error Prone, SonarQube.

## Local Development

### Prerequisites

- JDK 25.
- Docker and Docker Compose.
- Gradle wrapper is included.

### Start dependencies

```bash
docker compose up -d
```

This starts:

- PostgreSQL on `localhost:5432`.
- Keycloak on `localhost:8081`.
- SonarQube on `localhost:9000`.

### Run the application

```bash
./gradlew bootRun
```

The application starts on `localhost:8080`.

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON is available at:

```text
http://localhost:8080/api-docs
```

## Useful Commands

```bash
# Compile and run all verification tasks
./gradlew build

# Run tests
./gradlew test

# Apply formatting
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck

# Generate Jacoco reports
./gradlew jacocoTestReport

# Run SonarQube analysis after starting docker compose
./gradlew sonar
```

## Database

Liquibase migrations live in:

```text
src/main/resources/db/changelog/
```

The application uses `ddl-auto: validate`, so schema changes must be expressed as
Liquibase changelogs before the application can start successfully.

## OpenAPI

Inbound API contracts live in:

```text
specs/openapi/inbound/
```

Outbound event contracts live in:

```text
specs/openapi/outbound/
```

Generated sources are written under `build/generated/sources/openapi` during
compilation and are not committed.

## Evolution Strategy

The project should grow proportionally:

1. Keep one deployable Spring Boot application while the product is still taking
   shape.
2. Build each context as a clean vertical slice: domain, use cases, adapters,
   migrations, API contract, and tests.
3. Promote demo/scaffold packages to real modules only when their domain is
   implemented.
4. Split into independent services only when there is a concrete reason:
   separate scaling, independent release cadence, team ownership, or hard runtime
   isolation.

Until then, a disciplined modular monolith is the better engineering tradeoff.
