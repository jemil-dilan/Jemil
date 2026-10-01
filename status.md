# NYI — Project Status

Last updated: 2026-09-30 by Phase 1 status analysis (pre-Sprint-0 rebrand), revised the same day after `docs/NYI_MVP_Specification_Document_v1.1.docx` was added to the repo
Branch at time of analysis: `working/JEMIL-0` (identical to `main`, 0/0 divergence)
Build verified: `./gradlew compileJava` ✅ · `./gradlew test` ✅ 163 tests · `./gradlew e2eTest` ✅ 16 scenarios / 80 steps (hermetic, Testcontainers — no external DB needed)
Spec verified: `docs/NYI_MVP_Specification_Document_v1.1.docx` — all 12 sections present, complete, zero occurrences of "JEMIL"

> This file supersedes the old `docs/STATUS.md` (JEMIL-era, different sprint taxonomy). The old file is retained as historical record only; it is **not** the source of truth going forward.

---

## Sprint progress (per `docs/NYI_Sprint_Plan.md`)

| Sprint | Status | Notes |
|---|---|---|
| 0 — Rebrand JEMIL → NYI | **Not started** | 335 files still carry the old name. Blocking everything. |
| 1 — Foundations & Onboarding | **Not started** (partial prior art) | `agency` + `auth` exist but do not match ONB-01/02/03 (see below). |
| 2 — Trip Catalog & Fleet | **Not started** (partial prior art) | Trip aggregate exists inside the `booking` module; fleet is absent. |
| 3 — Booking Engine & Seat Inventory | **Not started** (partial prior art) | Seat hold + expiry exist; states and APIs do not match the spec. |
| 4 — Payments (MTN MoMo) & Reconciliation | Not started | `payment` module is an empty shell. |
| 5 — Ticket Issuance & Notification | Not started | `ticket` module is an empty shell. |
| 6 — Counter Sales & Cash | Not started | No `cash` module, no `CashRegisterSession`. |
| 7 — Controller, Boarding & Manifest | Not started | No `boarding` module. DB tables only. |
| 8 — Cancellation, Mass Refund | Not started | No refund code. DB tables only. |
| 9 — Scoped Dashboards & Reporting | Not started | No `reporting` module. |
| 10 — Hardening, Compliance, Pilot | Not started | — |

**Important:** sprints 1–3 are marked "Not started" against the *NYI spec*, but substantial legacy JEMIL code already implements adjacent functionality. That is prior art to be assessed and reused or refactored during those sprints — it is **not** credit toward the sprint's Definition of Done, because the domain model, states, and module boundaries do not match the spec.

---

## Use case status

| ID | Title | Implemented | Unit tested | E2E tested | Notes |
|---|---|---|---|---|---|
| ONB-01 | Onboard Operator + first Branch | Partial | No | Partial | Uses `Agency`/`AgencyBranch` aggregates instead of Operator/Branch. No commercial terms, no commission wiring. |
| ONB-02 | Onboard staff (User + membership) | **No** | No | No | `User` exists with global roles and no agency binding. `staff_users` table exists in Liquibase v8 with **zero** Java code. No hierarchy, no deactivation-blocking. |
| ONB-03 | Seed catalog (Cities & Routes) | Partial | Yes | Yes | `City` + `Route` CRUD and route search exist and pass. Naming differs (`City` not `Cities` table set), but functionally close. |
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
| — | Trip generation (rolling 14-day window) | Yes | Yes | Yes | **Not in the NYI spec** — legacy JEMIL functionality. Keep it; it is infrastructure, but flag it in the backlog per the sprint plan's traceability rule. |

---

## Architecture notes relevant to the next task

### Verified green baseline
- Java toolchain 25 (JDK at `/usr/lib/jvm/jdk-25.0.1-oracle-x64`; default `java` on PATH is 21 — Gradle resolves the toolchain itself, no action needed).
- Gradle 9.1.0, Spring Boot 4, PostgreSQL 15, Liquibase (12 changelogs, `v2` skipped), ArchUnit enforcing hexagonal boundaries (10 rules passing).
- 229 main + 52 test Java files. 163 unit tests, 0 failures. 16 Cucumber scenarios / 80 steps, 0 failures.
- e2e boots the app in-process on a random port and drives it over HTTP with RestAssured, profile `e2e`. **It is hermetic — it does not use the docker-compose Postgres.** `CucumberSpringConfiguration` starts its own Testcontainers `postgres:15-alpine` with hardcoded db `jemil_test`, user `jemil`, password `jemil_secret`, and overrides the datasource via `@DynamicPropertySource`. So `./gradlew e2eTest` needs no external database and no env vars. (Correction: I earlier claimed it required `DB_PORT=5433`; that was wrong — the variable is simply ignored by the e2e suite.)
- **The docker-compose dev Postgres is stale.** The volume was created 2026-08-13 and its `jemil_db` has only applied changelogs **v0–v7** — v8 through v12 never ran, so `trips`, `bookings`, `seat_assignments`, `buses`, `staff_users` do not exist, and the `t_*_demo` tables that v10 drops are still present. Nobody runs the app against it. It also publishes Postgres on host port **5433** while the app defaults to **5432**, so a manual local run needs an explicit `DB_PORT=5433` and will then trigger the missing migrations on first boot.
- Hardcoded DB identity that rebrand must touch: `CucumberSpringConfiguration.java:25-27` (`jemil_test` / `jemil` / `jemil_secret`), `docker-compose.yml` (`jemil-postgres`, `jemil_db`, `jemil`, `jemil_secret`), and the `application*.yml` defaults (`jemil_db`, `jemil_test`, `jemil_e2e`).

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
- `payment` / `ticket` are shells whose `SpringBeans` classes `@EntityScan` packages that do not exist.
- Six empty `@Configuration` classes named `JemilAuthbankBeans` (one per module) — legacy residue with no function.

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

### Known code defects found during this analysis
- `agency/.../UpdateBranchUseCaseImpl.java` builds a new `AgencyBranch` and returns it **without persisting** — a silent no-op. Its controller path returns 501 and is never reached, so the bug is currently masked.
- `AgencyController.updateBranch` / `deleteBranch` return 501 with `TODO` even though both use-case impls are wired in `SpringBeans` and simply never called.
- `SeatHoldService` catches `SeatHoldExecutor.SeatUnavailableException`, which is never thrown — dead catch block.
- Dead code: `Arrival`, `Departure` (never referenced), `domain/.../CommissionRate` (shadowed by a nested copy in `AgencyApplicationProperties`).
- `docker-compose.yml` still declares the obsolete `version: '3.8'` key.

---

## Known gaps / discrepancies found this session

1. **RESOLVED — the spec document is now present.** `docs/NYI_MVP_Specification_Document_v1.1.docx`
   was missing during the first pass of this analysis and has since been added to the
   repo (staged, not yet committed). It is complete: §1 Purpose, §2 Objectives/Non-Goals,
   §3 Strict Scope (3.1 in-scope, 3.2 deferred), §4 Actors & Roles incl. the
   `UserBranchMembership` decision, §5 Domain Model, §6 State Machines (Trip / Ticket /
   Cash Session), §7 UC-01…14, §7.5 SYS-01…05, §7.6 ONB-01…03, §8 Implementation
   Directions (8.1 approach, 8.2 module breakdown, 8.3 sequence, 8.4 critical rules),
   §9 NFRs, §10 Global Acceptance Checklist, §11 After MVP, §12 Final Directive.
   The section numbering the sprint plan cites (§3.1, §8.2, §10) all resolves correctly.
2. **NEW DISCREPANCY — the sprint plan and the spec disagree on sequencing.** The spec's
   own recommended order (§8.3) differs from `NYI_Sprint_Plan.md` in three places:
   - **UC-01 (search)** is not in the spec's §8.3 sequence as a build step at all, yet the
     sprint plan schedules it in Sprint 3 — *after* Sprint 2, which has UC-07 creating and
     opening trips. A trip that cannot be found is not demoable; the sprint plan's own
     Sprints 2 and 6 both depend on search existing.
   - **UC-08 (bus + driver)** sits at position 10 in spec §8.3 (after counter/cash), but
     the sprint plan pulls it forward into Sprint 2 as a Sprint 1 dependency.
   - **Audit** is position 15 in spec §8.3 (inside "Audit & hardening"), but the sprint
     plan requires an audit skeleton in Sprint 1 that every later module writes to.
   Not a contradiction of *scope* — all three UCs are in both documents — but the sprint
   plan is the operational plan and should be reconciled against §8.3 before Sprint 1
   starts, since the divergence changes what "done" means at each sprint boundary.
3. **NEW — the sprint plan omits one explicit ONB-02 requirement.** Spec ONB-02 states
   "Temporary delegation (limited permissions + expiry) is supported if needed for
   pilot." The sprint plan's ONB-02 step 4 covers the hierarchy rules but not temporary
   delegation. It is qualified with "if needed", so it is a judgement call — but it
   should be an explicit decision, not an omission.
4. **`docs/STATUS.md` is stale and off-taxonomy.** It is JEMIL-era (S0/S1/S1.5/S2… with
   `UC-P-*` / `UC-S-*` / `UC-C-*` IDs) and its use-case IDs do not map to the NYI
   `UC-01…14` / `SYS-01…05` / `ONB-01…03` scheme. It also names a branch
   (`chore/refactor-booking-step-restassured`) that is **12 commits behind** the current
   HEAD. Its claims were re-verified individually this session; the S1.5 checklist and the
   "20-parallel seat test" claim both hold up (see `SeatAssignmentConcurrencyIntegrationTest`).
5. **Sprint 0 is entirely undone.** 335 files still contain "jemil" (245 in `src/main`,
   53 in `src/test`). Not just package names — also: `rootProject.name = "jemil-backend"`,
   `group = "cm.jemil"`, Sonar project key/name, Docker container names, DB names
   (`jemil_db` / `jemil_test` / `jemil_e2e`), DB user `jemil`, the default JWT secret
   string, the Keycloak realm `jemil`, `spring.application.name`, all five OpenAPI titles,
   the production server URL `https://api.jemil.cm`, the README title, CI workflow env
   values, and Liquibase changeSet ids/authors (`JEMIL-2`, `jemil-dilan`, …).
6. **The git remote itself is still JEMIL**: `git@github.com:jemil-dilan/Jemil.git`.
   Sprint 0 step 1 requires renaming the repository. This is an action on GitHub, not in
   the codebase, and it needs the founder — flagging, not doing.
7. **User-facing booking reference prefix is `JML-`** (`BookingReference`). **Correction to
   my previous note:** I earlier wrote that the NYI spec's own examples use `JML-8F3K2`.
   That was wrong — I had picked the example up from the superseded JEMIL docs
   (`JEMIL_UC_Detail_Part1_Passenger_System.md:217`, `JEMIL_MVP_Build_Spec.md:275`).
   The NYI spec mentions a ticket "reference" as a concept in UC-03, UC-04, UC-09, UC-10
   and UC-14 but **never specifies a format or prefix**. So there is no spec-conformant
   behaviour to preserve, and `JML-` is unambiguously JEMIL residue. It is asserted by the
   e2e suite (`BookingStep.java:145`), so changing it is cheap — but it is user-facing and
   the founder should sign off on `NYI-` vs something else.
8. **Branch naming convention conflict.** The guideline mandates `working/NYI-<n>`; the
   repo already has ten `working/JEMIL-<n>` branches and is currently sitting on
   `working/JEMIL-0` (which equals `main`). Phase 3 requires starting from `main`.
9. **Two audit tables, no audit code.** `audit_logs` (v6) and `audit_log` (v8) both exist.
   Sprint 1 step 7 requires an audit module skeleton that every later module writes to.
10. **Dead DB schema with no code.** `staff_users`, `payments`, `refund_tasks`,
    `boarding_passes`, `sms_log`, `t_payment`, `t_payment_attempt`, `t_ticket`,
    `t_validation_record` are all created by Liquibase and referenced by no Java code.
    Hibernate runs with `ddl-auto: validate`, so these tables are load-bearing for the
    schema check even though they are functionally dead.
11. **Legacy JEMIL `schedules` table coexists with the MVP `trips` table** (carried over
    from the old STATUS.md and still true). Two overlapping trip concepts.
12. **`LocalDateTime` still used in 10+ main-source files** (mappers and agency domain
    objects), against the `Instant`-everywhere decision.
13. **NEW — 30+ Liquibase changeSet ids and authors still carry the JEMIL name**
    (`JEMIL-2`, `JEMIL-3:1`, `JEMIL-5`, `JEMIL-S0-TABLES`, `JEMIL-25`…`JEMIL-29`;
    authors `jemil-dilan`, `jemil-cli`, `njoupouandom_nfonka`, `codex`). **These must not
    be renamed.** Liquibase identifies a changeset by `(id, author, filename)`; changing
    either makes it a brand-new changeset, which would re-run the migration against a
    database where the tables already exist and break the whole changelog. This is an
    intentional, permanent exception to Sprint 0's zero-hits rule and belongs in an
    explicit allowlist.

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

1. Does the booking reference prefix stay `JML-` or become `NYI-`? (The spec does not say;
   `JML-` is JEMIL residue. Sprint 0's own rule says replace it.)
2. Should the GitHub repo `jemil-dilan/Jemil` be renamed, and to what exactly?
3. Should `docs/STATUS.md` and the other five JEMIL-era docs move to a `docs/archive/`
   folder, or stay put? Sprint 0 step 7 says archive rather than delete.
4. MoMo integration path (direct MTN vs aggregator) — Sprint 4 step 2 requires confirming
   whether a payment-status polling endpoint and a refund endpoint exist before any
   payment code is written. Also Sprint 0 step 6: the SMS sender ID must be re-registered
   under the NYI name, which can take days with some providers.
5. Sequencing: accept the sprint plan's order, or reconcile it against spec §8.3 first?
   (See discrepancy 2.)
6. Is ONB-02 temporary delegation in or out of the pilot?
7. Should `IN_TRANSIT` be dropped, or kept? Spec §6.1 has no such state; the sprint plan
   requires it. The spec is the declared source of truth, so my recommendation is to drop
   it and correct the sprint plan.

