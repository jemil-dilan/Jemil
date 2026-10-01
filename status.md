# NYI — Project Status

Last updated: 2026-10-01, after Sprint 0 (rebrand) completed on `working/NYI-1`
Previous revision: 2026-09-30, the Phase 1 pre-rebrand analysis (preserved in git history)

Build verified after the rebrand: `./gradlew test` ✅ 163 tests · `./gradlew e2eTest` ✅ 16 scenarios / 80 steps · `./scripts/verify_rebrand.sh` ✅ 0 hits outside the allowlist
Spec: `docs/NYI_MVP_Specification_Document_v1.1.docx` — all 12 sections present and complete

> This file supersedes the old `docs/STATUS.md`, which is pre-rebrand and uses a different sprint taxonomy. That file, and five other superseded documents, now live in [`docs/archive/`](docs/archive/) as historical record only. They are **not** the source of truth going forward.

---

## Sprint progress (per `docs/NYI_Sprint_Plan.md`)

| Sprint                                   | Status                              | Notes                                                               |
|------------------------------------------|-------------------------------------|---------------------------------------------------------------------|
| 0 — Rebrand to NYI                       | **Done**                            | See "Sprint 0 — what changed" below. Verified green.                |
| 1 — Foundations & Onboarding             | **Not started** (partial prior art) | `agency` + `auth` exist but do not match ONB-01/02/03 (see below).  |
| 2 — Trip Catalog & Fleet                 | **Not started** (partial prior art) | Trip aggregate exists inside the `booking` module; fleet is absent. |
| 3 — Booking Engine & Seat Inventory      | **Not started** (partial prior art) | Seat hold + expiry exist; states and APIs do not match the spec.    |
| 4 — Payments (MTN MoMo) & Reconciliation | Not started                         | `payment` module is an empty shell.                                 |
| 5 — Ticket Issuance & Notification       | Not started                         | `ticket` module is an empty shell.                                  |
| 6 — Counter Sales & Cash                 | Not started                         | No `cash` module, no `CashRegisterSession`.                         |
| 7 — Controller, Boarding & Manifest      | Not started                         | No `boarding` module. DB tables only.                               |
| 8 — Cancellation, Mass Refund            | Not started                         | No refund code. DB tables only.                                     |
| 9 — Scoped Dashboards & Reporting        | Not started                         | No `reporting` module.                                              |
| 10 — Hardening, Compliance, Pilot        | Not started                         | —                                                                   |

**Important:** sprints 1–3 are marked "Not started" against the *NYI spec*, but substantial pre-rebrand code already implements adjacent functionality. That is prior art to be assessed and reused or refactored during those sprints — it is **not** credit toward the sprint's Definition of Done, because the domain model, states, and module boundaries do not match the spec.

---

## Sprint 0 — what changed

A pure rename. No behaviour was intentionally changed; the test suite is unchanged at 163 unit tests and 16 e2e scenarios, all green before and after.

| Area | Change |
|---|---|
| Java packages | `cm.<old>` → `cm.nyi`, 281 files across main and test |
| Application class | Renamed to `NyiApplication`, plus every `classes = …Application.class` reference |
| Dead config | Deleted 6 empty per-module `*AuthbankBeans` classes (verified nothing referenced the bean names they registered) |
| Build | `rootProject.name`, `group`, Sonar project key/name, 9 OpenAPI generator package args, Cucumber `--glue`, feature directory, version catalog and Checkstyle headers |
| Tests | 10 ArchUnit rules, Cucumber Testcontainers identity |
| Config | `application.yml` + `dev`/`test`/`e2e` profiles, Keycloak realm, DB defaults, default JWT secret, logger levels |
| Docker | Container names, `POSTGRES_DB`/`USER`/`PASSWORD`, dropped the obsolete `version: '3.8'` key |
| Database | Role and database renamed on the dev instance; see below |
| API contracts | 9 OpenAPI specs: titles, descriptions, contacts, production server URL → `https://api.nyi.cm` |
| Booking reference | Prefix is now `NYI-` (was `JML-`), with the e2e assertion updated |
| Docs | 5 superseded founding docs + `docs/STATUS.md` → `docs/archive/` with an archive README; deployment guide renamed in place; README and docs map rewritten; 3 ADRs and the error-code catalogue rebranded |
| CI | `build.yml` service credentials, Qodana branch filter |
| Acceptance gate | `scripts/verify_rebrand.sh` (+ `.py` matcher) |

### Database rename — how it was done

The dev database was renamed for real, not by editing config and hoping. `scripts/rename_db_nyi.sql` is the migration, and it was executed against the running PostgreSQL instance and verified:

- Role renamed and database renamed; all 21 tables and their ownership carried over.
- `databasechangelog` re-read after the rename and confirmed intact — the pre-rebrand changeSet ids and authors are still recorded as applied.
- Re-ran the script to confirm it is a safe no-op the second time, then dropped the throwaway superuser.

Three constraints are documented in the script because all three were hit while testing it:

1. `ALTER DATABASE … RENAME TO` cannot run from inside the database being renamed, and **cannot** be a Liquibase changeset — Liquibase connects to exactly that database. Hence a standalone script.
2. `ALTER ROLE … RENAME TO` is rejected when the session user *is* that role, so the migration needs a separate superuser. The Compose stack has no second superuser because `POSTGRES_USER` is overridden, so the script documents a bootstrap-then-drop sequence.
3. `ALTER ROLE … RENAME TO` takes no `WITH` clause.

### Deliberate exceptions — the retired name stays here

Sprint 0's rule is zero hits outside a labelled archive folder. Three things are exempted on purpose, and `scripts/verify_rebrand.sh` encodes exactly these three:

| Location | Why it keeps the old name |
|---|---|
| `docs/archive/` | Historical founding documents. Archived, not deleted. |
| Liquibase `<changeSet>` opening tags in `src/main/resources/db/changelog/*.xml` | Liquibase keys a changeset by `(id, author, filename)`. Renaming an id or author turns it into a brand-new changeset and **re-runs the migration** against a database where the tables already exist. Rewriting the migration record is also a loss of audit trail. |
| `scripts/rename_db_nyi.sql` | The migration script has to name what it renames. It is inert and a no-op once applied. |

The gate matches multi-line `<changeSet>` tags correctly and never matches its own source. Note that only the *opening tag* is exempt — a comment, `<sql>` body, or table name in a changelog is still reported.

### Still open, cannot be done from the repository

- **Git remote is still under the old name.** The repository has to be renamed on GitHub; the founder is handling it.
- **SMS sender ID and MoMo merchant display name** must be re-registered under the NYI name. Provider lead time is days, so this needs starting before it blocks a launch.
- **Production database.** The dev instance is renamed and the script is proven, but the production instance has not been touched. Its name is referenced in `docs/deployment/Sprint-1-Deployment-Guide.md` and is currently `<old>_production`; run the same script there before deploying.

---

## Use case status

| ID | Title | Implemented | Unit tested | E2E tested | Notes |
|---|---|---|---|---|---|
| ONB-01 | Onboard Operator + first Branch | Partial | No | Partial | Uses `Agency`/`AgencyBranch` aggregates instead of Operator/Branch. No commercial terms, no commission wiring. |
| ONB-02 | Onboard staff (User + membership) | **No** | No | No | `User` exists with global roles and no agency binding. `staff_users` table exists in Liquibase v8 with **zero** Java code. No hierarchy, no deactivation-blocking. |
| ONB-03 | Seed catalog (Cities & Routes) | Partial | Yes | Yes | `City` + `Route` CRUD and route search exist and pass. Naming differs, but functionally close. |
| UC-01 | Search available trips | Partial | Yes | Yes | `GET /trips/search` exists. Does not filter to `OPEN_FOR_SALE` only (Trip has no such state). |
| UC-02 | Reserve a seat (online) | Partial | Yes | Yes | Hold API + 20-parallel concurrency test passing. Passenger details not collected (UC-P-04 absent). |
| UC-03 | Pay with MTN MoMo | **No** | No | No | `payment` module is an empty shell. No HTTP client dependency at all. |
| UC-04 | Issue ticket (QR + SMS) | **No** | No | No | `ticket` module empty. `t_ticket` / `boarding_passes` tables exist unused. |
| UC-05 | Counter sale (guichet) | **No** | No | No | No counter path, no cash session. |
| UC-06 | Open / close cash session | **No** | No | No | No `CashRegisterSession` anywhere. |
| UC-07 | Create and open a Trip | Partial | Yes | Yes | Trip lifecycle in code is `OPEN → BOARDING → DEPARTED → ARRIVED → CANCELLED`. Spec §6.1 requires `DRAFT`, `SCHEDULED`, `OPEN_FOR_SALE` — all three missing or misnamed. See the exact state diff below. |
| UC-08 | Assign bus and driver | Partial | No | No | `Bus` aggregate + `buses` table exist. **No `Driver` entity at all.** No trip-level assignment record, no reassignment history. |
| UC-09 | Control & boarding (online + offline) | **No** | No | No | No device model, no offline queue, no dedup IDs. `t_validation_record` table unused. |
| UC-10 | Generate manifest | **No** | No | No | — |
| UC-11 | Basic refund | **No** | No | No | `refund_tasks` table unused. |
| UC-12 | Diaspora purchase (Buyer ≠ Passenger) | **No** | No | No | `Booking` has `PassengerInfo` only; no separate Buyer field. |
| UC-13 | Scoped dashboards | **No** | No | No | — |
| UC-14 | Passenger ticket lookup / resend | **No** | No | No | — |
| SYS-01 | Seat hold expiry | Partial | Yes | Yes | Job + Cucumber simulated tick pass. **Payment-aware skip is not implemented** (explicitly deferred in code comment) because no payment module exists. The spec's payment-vs-expiry race rule is therefore untested and unimplemented. |
| SYS-02 | Payment reconciliation / webhook recovery | **No** | No | No | — |
| SYS-03 | Automatic mass refund on cancellation | **No** | No | No | — |
| SYS-04 | SMS delivery failure / retry | **No** | No | No | `sms_log` table unused. No SMS adapter or port. |
| SYS-05 | Manifest derivation | **No** | No | No | — |
| — | Trip generation (rolling 14-day window) | Yes | Yes | Yes | **Not in the NYI spec** — pre-rebrand functionality. Keep it; it is infrastructure, but flag it in the backlog per the sprint plan's traceability rule. |

---

## Architecture notes relevant to the next task

### Verified green baseline
- Java toolchain 25 (JDK at `/usr/lib/jvm/jdk-25.0.1-oracle-x64`; default `java` on PATH is 21 — Gradle resolves the toolchain itself, no action needed).
- Gradle 9.1.0, Spring Boot 4, PostgreSQL 15, Liquibase (12 changelogs, `v2` skipped), ArchUnit enforcing hexagonal boundaries (10 rules passing).
- 229 main + 52 test Java files. 163 unit tests, 0 failures. 16 Cucumber scenarios / 80 steps, 0 failures.
- e2e boots the app in-process on a random port and drives it over HTTP with RestAssured, profile `e2e`. **It is hermetic — it does not use the docker-compose Postgres.** `CucumberSpringConfiguration` starts its own Testcontainers `postgres:15-alpine` and overrides the datasource via `@DynamicPropertySource`, so `./gradlew e2eTest` needs no external database and no env vars. (An earlier note claimed it required `DB_PORT=5433`; that was wrong — the variable is simply ignored by the e2e suite.)
- **The docker-compose dev Postgres carries an incomplete schema.** Its volume was created 2026-08-13 and had only applied changelogs **v0–v7** — v8 through v12 never ran, so `trips`, `bookings`, `seat_assignments`, `buses`, `staff_users` do not exist, and the `t_*_demo` tables that v10 drops are still present. It now holds only v0–v7 plus the post-rename identity; nothing runs the app against it. It also publishes Postgres on host port **5433** while the app defaults to **5432**, so a manual local run needs an explicit `DB_PORT=5433` and will then trigger the missing migrations on first boot.

### Module layout vs spec §8.2
The spec's §8.2 modules are: `organization`, `catalog`, `fleet`, `trip`, `booking`, `payment`, `cash`, `boarding`, `notification`, `jobs`, `reporting`, `audit`.

What actually exists: `auth`, `agency`, `booking`, `payment` (shell), `ticket` (shell), `trip` (shell), `shared`.

Mapping problems the next task must respect:
- `Trip` lives in **`booking.domain.trip`**, while a `trip` module also exists as an **empty shell** — two homes for one concept. Spec puts `catalog`/`trip` ownership elsewhere.
- `organization` ≈ the current `agency` module, but the vocabulary differs (`Agency` vs `Operator`, `AgencyBranch` vs `Branch`).
- `catalog` (Cities/Routes) is currently inside `agency` rather than its own module.
- `fleet` has no module; `Bus` sits in `booking.domain.bus`.
- `jobs` has no module; the three schedulers live inside `booking` and `shared`.
- `audit` has no module and **no Java code**, despite `audit_logs` and `audit_log` tables existing.
- `payment` / `ticket` / `trip` are shells whose `SpringBeans` classes `@EntityScan` packages that **do not exist** on disk. Verified during Sprint 0; this was not caused by the rebrand.

### State machine diff vs spec §6 (exact)

`booking/domain/trip/TripStatus.java` vs spec §6.1:

| Spec §6.1 | In code? |
|---|---|
| `DRAFT` | Missing |
| `SCHEDULED` | Missing |
| `OPEN_FOR_SALE` | Present but named `OPEN` |
| `BOARDING` | ✅ |
| `DEPARTED` | ✅ |
| `IN_TRANSIT` | **Does not exist in the spec at all** — but the sprint plan's Sprint 2 step 2 lists it as a required state. The spec goes `DEPARTED → ARRIVED` directly. The two planning documents disagree; the spec wins as source of truth, so `IN_TRANSIT` should not be built unless the founder says otherwise. |
| `ARRIVED` | ✅ |
| `CANCELLED` | ✅ |

`booking/domain/booking/BookingStatus.java` vs spec §6.2:

| Spec §6.2 | In code? |
|---|---|
| `HELD` | ✅ |
| `PENDING_PAYMENT` | ✅ |
| `CONFIRMED` | Present but named `PAID` |
| `ISSUED` | Missing |
| `CHECKED_IN` | Missing |
| `BOARDED` | Missing |
| `COMPLETED` | Missing |
| `CANCELLED` / `REFUNDED` | `CANCELLED` ✅ / `REFUNDED` missing |
| `NO_SHOW` / `EXPIRED` | `EXPIRED` ✅ / `NO_SHOW` missing |

The two present-but-misnamed states (`OPEN`, `PAID`) are the dangerous ones: they look
conformant on a skim and will silently propagate into the payment, ticketing and manifest
work if not renamed first.

### Security / scoping
- `SecurityConfig` authenticates but does **not** authorize. `@EnableMethodSecurity` is on; there are **zero** `@PreAuthorize`/`@Secured`/`@RolesAllowed` annotations in the codebase.
- All GETs on agency/routes/cities/trips and `POST /bookings` are `permitAll()`.
- JWT roles are issued into `ROLE_*` authorities and then never read. No branch scoping, no tenant scoping, no scope enum. Sprint 1 step 6 and Sprint 9 are entirely unaddressed.

### External integrations
**None exist.** No HTTP client, no QR library, no SMS/MoMo SDK, no port interfaces. `.env` declares `MOMO_*`, `STRIPE_*`, `AFRICAS_TALKING_*` placeholders referenced by no code.

### Known code defects found during analysis
- `agency/.../UpdateBranchUseCaseImpl.java` builds a new `AgencyBranch` and returns it **without persisting** — a silent no-op. Its controller path returns 501 and is never reached, so the bug is currently masked.
- `AgencyController.updateBranch` / `deleteBranch` return 501 with `TODO` even though both use-case impls are wired in `SpringBeans` and simply never called.
- `SeatHoldService` catches `SeatHoldExecutor.SeatUnavailableException`, which is never thrown — dead catch block.
- Dead code: `Arrival`, `Departure` (never referenced), `domain/.../CommissionRate` (shadowed by a nested copy in `AgencyApplicationProperties`).

---

## Known gaps / discrepancies

1. **RESOLVED — the spec document is present.** `docs/NYI_MVP_Specification_Document_v1.1.docx`
   is in the repo and complete: §1 Purpose, §2 Objectives/Non-Goals, §3 Strict Scope, §4 Actors
   & Roles incl. the `UserBranchMembership` decision, §5 Domain Model, §6 State Machines,
   §7 UC-01…14, §7.5 SYS-01…05, §7.6 ONB-01…03, §8 Implementation Directions, §9 NFRs,
   §10 Global Acceptance Checklist, §11 After MVP, §12 Final Directive.
2. **OPEN — the sprint plan and the spec disagree on sequencing.** The spec's recommended order
   (§8.3) differs from `NYI_Sprint_Plan.md` in three places:
   - **UC-01 (search)** is not in the spec's §8.3 sequence as a build step at all, yet the
     sprint plan schedules it in Sprint 3 — *after* Sprint 2, which has UC-07 creating and
     opening trips. A trip that cannot be found is not demoable.
   - **UC-08 (bus + driver)** sits at position 10 in spec §8.3, but the sprint plan pulls it
     forward into Sprint 2 as a Sprint 1 dependency.
   - **Audit** is position 15 in spec §8.3, but the sprint plan requires an audit skeleton in
     Sprint 1 that every later module writes to.

   Not a contradiction of *scope* — all three are in both documents — but the sprint plan is
   the operational plan and should be reconciled against §8.3 before Sprint 1 starts, since
   the divergence changes what "done" means at each sprint boundary.
3. **OPEN — the sprint plan omits one explicit ONB-02 requirement.** Spec ONB-02 states
   "Temporary delegation (limited permissions + expiry) is supported if needed for pilot."
   The sprint plan's ONB-02 step 4 covers hierarchy rules but not temporary delegation. It is
   qualified with "if needed", so it is a judgement call — but it should be an explicit
   decision, not an omission.
4. **RESOLVED — the superseded status board.** The pre-rebrand `docs/STATUS.md` used an
   off-taxonomy ID scheme that does not map to `UC-01…14` / `SYS-01…05` / `ONB-01…03`, and
   named a branch 12 commits behind HEAD. Its claims were re-verified individually; the S1.5
   checklist and the "20-parallel seat test" claim both hold up. The file is now archived.
5. **RESOLVED — Sprint 0 rebrand.** Completed and verified; see "Sprint 0 — what changed".
   Measured baseline was 1280 occurrences across 326 files; now 0 outside the allowlist.
6. **OPEN — the git remote is still under the old name.** Sprint 0 step 1 requires renaming
   the repository. This is an action on GitHub, not in the codebase, and it needs the founder.
7. **RESOLVED — booking reference prefix.** The spec never specifies a format or prefix for the
   booking reference; it only mentions a "reference" as a concept. The pre-rebrand `JML-` prefix
   was residue from the superseded docs, so Sprint 0 replaced it with `NYI-` and updated the
   e2e assertion in `BookingStep`.
8. **RESOLVED — branch naming.** The guideline mandates `working/NYI-<n>`. Ten branches from the
   old convention still exist and were left untouched; all new work goes on `working/NYI-<n>`.
   Sprint 0 was branched from `main`, not from a legacy branch.
9. **Two audit tables, no audit code.** `audit_logs` (v6) and `audit_log` (v8) both exist.
   Sprint 1 step 7 requires an audit module skeleton that every later module writes to.
10. **Dead DB schema with no code.** `staff_users`, `payments`, `refund_tasks`,
    `boarding_passes`, `sms_log`, `t_payment`, `t_payment_attempt`, `t_ticket`,
    `t_validation_record` are all created by Liquibase and referenced by no Java code.
    Hibernate runs with `ddl-auto: validate`, so these tables are load-bearing for the
    schema check even though they are functionally dead.
11. **A legacy `schedules` table coexists with the MVP `trips` table.** Two overlapping trip
    concepts carried over from before the rebrand.
12. **`LocalDateTime` still used in 10+ main-source files** (mappers and agency domain
    objects), against the `Instant`-everywhere decision.
13. **DELIBERATE, PERMANENT — pre-rebrand Liquibase changeSet ids and authors are untouched.**
    Roughly 30 ids and 4 authors still carry the old name. This is intentional and enforced by
    the acceptance gate's allowlist. Liquibase keys a changeset by `(id, author, filename)`;
    renaming either would re-run the migration against a database where the tables already
    exist and corrupt the changelog. The audit trail wins over cosmetic consistency.

---

## Global Acceptance Checklist traceability (spec §10)

The spec's §10 is the gate for Sprint 10. Current state of each of its 16 items:

| # | Checklist item | State |
|---|---|---|
| 1 | Passenger can buy a ticket end-to-end without an account | Blocked — no payment |
| 2 | Counter sale uses the same seat inventory as online | Blocked — no counter path |
| 3 | QR + SMS ticket can be validated by controller | Blocked — no ticket module |
| 4 | Controller works offline and synchronizes correctly | Blocked — no boarding module |
| 5 | Cash session produces expected vs actual; variance needs supervisor | Blocked — no cash module |
| 6 | Manifest matches issued tickets, generated automatically | Blocked — no manifest |
| 7 | Branch Manager cannot see other branches | **Not started, and currently violated** — no authorization exists |
| 8 | Bus and driver can be replaced with full audit trail | Partial — `Bus` exists, no `Driver`, no history |
| 9 | Seat holds expire automatically; race with payment handled safely | Partial — expiry works; the payment race is unimplemented and untestable today |
| 10 | Stuck payments are reconciled automatically | Blocked — no payment module |
| 11 | Trip cancellation triggers automatic refund attempts | Blocked — no refund code |
| 12 | SMS failures retried; passenger can recover ticket via lookup | Blocked — no SMS, no UC-14 |
| 13 | Operator, Branch and Staff onboarded through defined flows | Partial — `Agency`/`AgencyBranch` only; no membership |
| 14 | All sensitive actions are attributable | **Not started** — audit tables exist, no audit code |
| 15 | MTN MoMo works in production conditions | Blocked — no payment module |
| 16 | Passenger personal data handling consistent with Law 2024/017 | Not started |

Items 7, 9 and 14 are the ones most at risk of being quietly skipped, because partial
scaffolding makes them *look* closer to done than they are.

---

## Open decisions for the founder

1. ~~Does the booking reference prefix stay `JML-` or become `NYI-`?~~ **Decided in Sprint 0:
   `NYI-`.** The spec specifies no format; the old prefix was pre-rebrand residue.
2. **OPEN — should the GitHub repository be renamed, and to what exactly?** Sprint 0 could not
   do this. Currently still under the old owner and name.
3. ~~Should the superseded docs be archived or left in place?~~ **Decided in Sprint 0:
   archived** to `docs/archive/` with an archive README, per the sprint plan.
4. **OPEN — MoMo integration path** (direct MTN vs aggregator). Sprint 4 step 2 requires
   confirming whether a payment-status polling endpoint and a refund endpoint exist before any
   payment code is written. Also: the SMS sender ID must be re-registered under the NYI name,
   which can take days with some providers.
5. **OPEN — sequencing.** Accept the sprint plan's order, or reconcile it against spec §8.3
   first? (See gap 2.)
6. **OPEN — is ONB-02 temporary delegation in or out of the pilot?**
7. **OPEN — should `IN_TRANSIT` be dropped, or kept?** Spec §6.1 has no such state; the sprint
   plan requires it. The spec is the declared source of truth, so the recommendation is to drop
   it and correct the sprint plan. Not actioned in Sprint 0: it is a domain-model change, not a
   rename.
8. **NEW — production database rename.** The dev instance is migrated and the script is proven,
   but production has not been touched. Confirm the production database and role names, then run
   the same script in the same change as the config update.