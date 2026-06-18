# Entity changelog and necessary DB / integration steps

Date: 2026-06-13

This document summarizes the current JPA entity classes found in the codebase (mainly `DemoJpa` in several modules), the recommended database migration SQL or Liquibase snippets to create the corresponding tables, and a checklist of "necessary" follow-up actions (application / tests / CI) to keep these entities production-ready.

---

## Summary of entities discovered

All discovered entities are demo placeholders with the same shape across modules. They live per-module under `cm.jemil.<module>.adapter.outbond.persistence.jpa.entity`.

Entities found:

- `cm.jemil.auth.adapter.outbond.persistence.jpa.entity.DemoJpa`
  - Entity name: `AuthDemoJpa`
  - Table: `t_auth_demo`
  - Fields:
    - `UUID id` — annotated with `@Id`, column `c_id`
    - `String name` — column `c_name`
  - Equality: equals/hashCode rely on `id` only.

- `cm.jemil.agency.adapter.outbond.persistence.jpa.entity.DemoJpa`
  - Entity name: `AgencyDemoJpa`
  - Table: `t_agency_demo`
  - Fields: same as above (id/name)

- `cm.jemil.booking.adapter.outbond.persistence.jpa.entity.DemoJpa`
  - Entity name: `BookingDemoJpa`
  - Table: `t_booking_demo`
  - Fields: same as above (id/name)

- `cm.jemil.payment.adapter.outbond.persistence.jpa.entity.DemoJpa`
  - Entity name: `PaymentDemoJpa`
  - Table: `t_payment_demo`
  - Fields: same as above (id/name)

- `cm.jemil.ticket.adapter.outbond.persistence.jpa.entity.DemoJpa`
  - Entity name: `TicketDemoJpa`
  - Table: `t_ticket_demo`
  - Fields: same as above (id/name)

Notes:
- The JPA classes use `jakarta.persistence` annotations and Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@FieldNameConstants`).
- There is no `@GeneratedValue` on the `id` field. That implies the application is expected to set the `UUID` before persisting.

---

## Recommended DB migrations (SQL / Flyway style)

Below are example SQL migrations to create the demo tables. Adjust data types and defaults for your RDBMS (examples here use PostgreSQL syntax for `UUID` and `gen_random_uuid()` where convenient). Filename examples for Flyway: `V1__create_demo_tables.sql` (or separate files per module `V1_1__create_auth_demo_table.sql`, etc.).

### Example (PostgreSQL) — single migration creating all demo tables

```sql
-- V1__create_demo_tables.sql
-- Make sure the pgcrypto extension is enabled if you want gen_random_uuid():
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS t_auth_demo (
  c_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  c_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_agency_demo (
  c_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  c_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_booking_demo (
  c_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  c_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_payment_demo (
  c_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  c_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_ticket_demo (
  c_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  c_name VARCHAR(255) NOT NULL
);

-- Optional indexes if you will query by c_name frequently
CREATE INDEX IF NOT EXISTS idx_auth_demo_name ON t_auth_demo (c_name);
CREATE INDEX IF NOT EXISTS idx_agency_demo_name ON t_agency_demo (c_name);
CREATE INDEX IF NOT EXISTS idx_booking_demo_name ON t_booking_demo (c_name);
CREATE INDEX IF NOT EXISTS idx_payment_demo_name ON t_payment_demo (c_name);
CREATE INDEX IF NOT EXISTS idx_ticket_demo_name ON t_ticket_demo (c_name);
```

### Liquibase XML snippet (single changeSet example)

```xml
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                       http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">

  <changeSet id="20260613-create-demo-tables" author="dev">
    <createTable tableName="t_auth_demo">
      <column name="c_id" type="UUID">
        <constraints primaryKey="true" nullable="false"/>
      </column>
      <column name="c_name" type="varchar(255)">
        <constraints nullable="false"/>
      </column>
    </createTable>

    <!-- Repeat for other demo tables: t_agency_demo, t_booking_demo, t_payment_demo, t_ticket_demo -->

  </changeSet>
</databaseChangeLog>
```

Notes on UUID generation:
- If you prefer the database to generate UUIDs, use a DB default (e.g. `DEFAULT gen_random_uuid()` on Postgres). Ensure the DB has the appropriate extension (pgcrypto) or use `uuid-ossp` functions.
- Alternatively, generate UUIDs in the application (e.g. set `entity.setId(UUID.randomUUID())` before save) and keep columns without DB default.

---

## Recommended code changes / safeguards (application-side)

1. Explicitly document or enforce where UUIDs are generated:
   - If DB-generated: add `@Column(name = "c_id", nullable = false)
     @GeneratedValue` (note: JPA support for DB UUID generation varies by provider) or leave `@Id` and rely on DB default; update repositories accordingly.
   - If application-generated (recommended simple approach): ensure service layer sets `id` with `UUID.randomUUID()` before persisting.

2. Make constraints explicit in entities where appropriate:
   - Add `@Column(name = "c_name", nullable = false, length = 255)` on `name` fields to reflect DB constraints.

3. Consider using `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` or Lombok `@EqualsAndHashCode` with `@EqualsAndHashCode.Include` on `id` for consistency and to avoid manual implementations. Current manual equals/hashCode based on `id` is acceptable but ensure it's consistent across entities.

4. Add `@GeneratedValue` alternative if you want JPA to generate UUIDs:
   - Example using Hibernate UUID generator:
     ```java
     @Id
     @GeneratedValue(generator = "UUID")
     @GenericGenerator(
       name = "UUID",
       strategy = "org.hibernate.id.UUIDGenerator"
     )
     @Column(name = "c_id", updatable = false, nullable = false)
     private UUID id;
     ```
   - This requires `org.hibernate.annotations.GenericGenerator` import and Hibernate as provider.

5. Make `@Table` and column names consistent (they already follow `t_<module>_demo` and `c_` prefixes; keep consistent naming conventions in future entities).

---

## Tests and CI (necessary additions / checks)

- Add unit tests for repository layer (using an in-memory DB like H2 or Testcontainers with Postgres) to verify reading/writing entities (especially UUID handling).
- Add integration tests for service layer to ensure id generation strategy works as intended.
- Add schema migration verification in CI (run Flyway/Liquibase validate or migrate on a disposable DB before running tests).
- Add acceptance tests for endpoints mapping to these demo entities (if they are exposed in REST). Cucumber is already present for e2e; reuse it.

---

## Mapping & DTOs

- The project already contains generated DTOs under `cm.jemil.generated.*` and mappers (e.g. `AuthDemoJpaMapper`, `AgencyDemoJpaMapper`, etc.). Ensure the mapper tests exist to assert mapping correctness.
- If you plan to add additional fields to entities, update DTOs and mapping templates accordingly.

---

## Checklist of "necessaries" (actionable tasks)

- [ ] Add DB migration(s) for demo tables (Flyway or Liquibase). Example SQL provided above.
- [ ] Decide and standardize UUID generation strategy (DB vs app) and document it in `README.md` and code (annotate entities / set IDs in service layer).
- [ ] Add `nullable = false` / `length` metadata to entity columns to match DB constraints.
- [ ] Add repository and mapper unit tests (Testcontainers/H2) if not present.
- [ ] Add schema validation step to CI (run migrations against a disposable DB) before running tests.
- [ ] If generated sources should be checked/ formatted, decide whether to: a) exclude generated code from strict linters (Checkstyle/ErrorProne), b) post-process generated code with a formatter, or c) adjust generator templates.

---

## Example migration file names to add to VCS

- `src/main/resources/db/migration/V1__create_demo_tables.sql` (Flyway)
- or `db/changelog/20260613-create-demo-tables.xml` (Liquibase)

Add those files to the repository and ensure the app's datasource/migration plugin runs them on startup or CI.

---

If you want, I can now:
- Create the actual Flyway SQL migration file in the repo at `src/main/resources/db/migration/V1__create_demo_tables.sql` with the SQL shown above.
- Add small entity improvements (e.g., add `nullable=false` on `c_name` columns in the Java classes and/or add a UUID generator annotation) and run compilation/tests.
- Generate per-module changelog entries or a Git-style changelog from commit history (if you want audit-style changelog rather than schema changelog).

Which one should I do next? (create migration file / add entity annotations / run tests / generate git changelog)
