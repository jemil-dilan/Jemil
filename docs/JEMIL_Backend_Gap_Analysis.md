# JEMIL Backend — Gap Analysis & Corrected Plan

**Version:** 1.0 · **Date:** 12 August 2026
**Reviewed:** `README.md`, `JEMIL_Backend_Analysis.md`, `JEMIL_Project_Structure.md`, `JEMIL_Project_Structure_By_Me.md`, `JEMIL_Sprint_Plan.md`, `CODE_REVIEW_REPORT.md`
**Reconciled against:** `JEMIL_UC_Detail_Part1_Passenger_System.md`, `JEMIL_UC_Detail_Part2_Staff_Apps.md`, `JEMIL_Use_Cases_Acceptance_Criteria.md`

---

## 1. Honest status

| Claim in your documents | Reality |
|---|---|
| "Production-ready" agency module | Accurate. Real domain, real adapters, real migrations. |
| "Exemplary engineering discipline" | Half true. The *structure* is excellent. 17 test files against 197 source files, with a Definition of Done demanding ≥70% coverage, is not. |
| "Ready for rapid feature development" | Only after the model defects in §2 are fixed. Building booking on the current `Schedule` will require reworking it later. |
| "8–11 weeks to MVP" | Backend-only, and roughly right. Excludes all client applications. See §5. |
| 9 modules | 1 implemented, 8 scaffolds. |

**What you have actually built:** a very good skeleton, one complete vertical slice, and the CI/quality apparatus of a much larger project.

**What that means:** the second module will be far faster than the first, because the patterns are proven. This is the normal and correct shape of a well-run project at this stage. The risk is not the scaffolding — it is that three of the foundation models are wrong, and every subsequent module inherits them.

A note on the documents themselves: `CODE_REVIEW_REPORT.md` and `JEMIL_Backend_Analysis.md` read as machine-generated, and their superlatives ("exemplary", "outstanding", "benchmark for Hexagonal modular monoliths") are doing you a disservice. §4 below re-grades the findings. One of the two "critical" items is misdiagnosed in a way that would have you fix the right code for the wrong reason.

---

## 2. Model defects to fix before Sprint 2

These are cheap now and expensive after payment and ticketing depend on them.

### D1 · Seat inventory is a counter, not an assignment table

**Current**

```java
Schedule (Entity)
├── ScheduleId
├── departureTime: LocalDateTime
├── totalSeats: int
└── availableSeats: int      // @Version on ScheduleJpaEntity
```

**Why it fails.** Your product sells *named* seats — the passenger taps seat 12. A counter cannot represent that. And `@Version` on the `Schedule` aggregate produces exactly the wrong concurrency semantics:

- Two passengers booking **different** seats on the same bus → version conflict → one fails for no reason. On a 70-seat bus during morning rush this happens constantly.
- Nothing in the schema prevents two bookings of **the same** seat. The invariant you need is not the one being enforced.

Optimistic locking on an aggregate whose contention is per-seat is a design error, not a tuning problem. ADR-4 ("optimistic locking, better for read-heavy workloads") reasoned about the wrong contention pattern.

**Fix**

```sql
CREATE TABLE seat_assignments (
    id           UUID PRIMARY KEY,
    trip_id      UUID NOT NULL REFERENCES trips(id),
    seat_no      INTEGER NOT NULL,
    booking_id   UUID NOT NULL REFERENCES bookings(id),
    status       VARCHAR(16) NOT NULL,   -- HELD | SOLD | RELEASED
    created_at   TIMESTAMPTZ NOT NULL
);

-- The single most important line in the schema.
CREATE UNIQUE INDEX seat_once_per_trip
    ON seat_assignments (trip_id, seat_no)
    WHERE status IN ('HELD', 'SOLD');
```

Correctness now lives in the database, where a race cannot slip past it. `Schedule.availableSeats` becomes a derived read value, not the source of truth — keep it as a denormalised counter for fast search listings if you like, but never book against it.

**JPA detail that will bite you.** Hibernate defers the INSERT until flush. Without an explicit flush the constraint violation surfaces at commit, when the transaction is already unusable and you cannot translate it cleanly into a 409. Force it:

```java
@Transactional
public BookingResult holdSeats(TripId tripId, List<Integer> seatNos, ...) {
    Booking booking = Booking.hold(tripId, seatNos, passenger, HOLD_TTL);
    bookingRepository.save(booking);
    try {
        seatAssignmentRepository.saveAll(booking.seatAssignments());
        entityManager.flush();                 // surface the violation here
    } catch (DataIntegrityViolationException e) {
        if (isSeatConstraint(e)) {             // inspect the constraint name
            throw new SeatUnavailableException(tripId, seatNos);
        }
        throw e;
    }
    return BookingResult.of(booking);
}
```

Check the constraint *name* — `seat_once_per_trip` — rather than catching every integrity violation, or you will convert unrelated bugs into misleading "seat taken" responses.

**Test that must pass before Sprint 2 closes** (UC-P-03 AC-6): 20 parallel requests for the same seat → exactly 1 success, 19 conflicts, 1 row in the table. Run it against real PostgreSQL via Testcontainers. An H2 test proves nothing here.

---

### D2 · `Schedule` conflates the recurring pattern with the dated departure

**Current** (ticket S1-03): `departureTime`, `arrivalTime`, `daysOfWeek`, `availableSeats` on one entity.

`daysOfWeek` describes a repeating rule. `availableSeats` describes one specific bus on one specific morning. A seat cannot be sold on "Mondays at 06:00."

**Fix — two tables**

```sql
CREATE TABLE schedule_templates (
    id                UUID PRIMARY KEY,
    route_id          UUID NOT NULL REFERENCES routes(id),
    bus_id            UUID NOT NULL REFERENCES buses(id),
    departure_time    TIME NOT NULL,          -- time of day only
    days_of_week      SMALLINT NOT NULL,      -- bitmask, Mon=1
    price_xaf         INTEGER NOT NULL,
    travel_class      VARCHAR(16) NOT NULL,
    active            BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE trips (
    id                UUID PRIMARY KEY,
    template_id       UUID REFERENCES schedule_templates(id),  -- null for one-offs
    agency_id         UUID NOT NULL REFERENCES agencies(id),
    route_id          UUID NOT NULL REFERENCES routes(id),
    bus_id            UUID NOT NULL REFERENCES buses(id),
    departure_at      TIMESTAMPTZ NOT NULL,   -- concrete instant
    service_date      DATE NOT NULL,
    price_xaf         INTEGER NOT NULL,
    travel_class      VARCHAR(16) NOT NULL,
    status            VARCHAR(16) NOT NULL,
    seats_total       INTEGER NOT NULL,
    seats_sold        INTEGER NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX trip_once_per_template_date
    ON trips (template_id, service_date)
    WHERE template_id IS NOT NULL;
```

A nightly job materialises a rolling 14-day window (UC-S-06). Without it, someone hand-creates ~240 records a month for two agencies, and that breaks in week two.

This also gives you cancellation granularity: cancel one Tuesday's departure without touching the schedule.

---

### D3 · Money as `double`

```java
Route.price: double     // current
```

Never represent money as a floating-point type. XAF has no minor unit, so this one is easy:

```java
public record Money(int amountXaf) {
    public Money {
        if (amountXaf < 0) throw new IllegalArgumentException("Negative amount");
    }
    public Money times(int qty) { return new Money(Math.multiplyExact(amountXaf, qty)); }
}
```

`INTEGER` in the database. Fix it before payment code multiplies prices by seat counts and reconciliation starts comparing your totals against MTN's.

---

### D4 · `LocalDateTime` for instants

`CreatedAt` wraps `LocalDateTime`, and Error Prone is already warning about `JavaTimeDefaultTimeZone` in generated mappers. Those warnings are correct and they are not noise.

`LocalDateTime` has no timezone. Your server, your database, and Cameroon (UTC+1) may disagree. UC-S-06 must generate trips by Africa/Douala calendar days; a job reasoning in UTC will place departures on the wrong day near midnight.

**Rule:** `Instant` for every timestamp (`created_at`, `departure_at`, `boarded_at`). `LocalDate`/`LocalTime` only for genuinely calendar-local values (`service_date`, template `departure_time`). Store `TIMESTAMPTZ`. Convert to `Africa/Douala` at the presentation boundary only.

---

### D5 · Payment states omit the two that matter most

**Current:** `INITIATED → PENDING → CONFIRMED | FAILED | REFUNDED`

**Missing:** `TIMEOUT` and `RECONCILED_LATE`.

Your sprint plan has no ticket for UC-S-03: *the payment succeeded at T+180s, after you gave up at T+120s, and the seat has since been resold.* You are holding a passenger's money with no ticket to give them. S3-11's reconciliation job polls `PENDING` payments — it does not cover this branch.

```java
public enum PaymentStatus {
    INITIATED,
    PENDING,
    CONFIRMED,
    FAILED,
    TIMEOUT,           // we stopped waiting; outcome genuinely unknown
    RECONCILED_LATE,   // succeeded after timeout, seat still free, ticket issued
    REFUND_DUE,        // succeeded after timeout, seat gone, money must go back
    REFUNDED
}
```

Two outcomes only, always: **ticket delivered, or money returned.** There is no third branch, and there is never a state where JEMIL keeps the money silently.

Also note: a `TIMEOUT` is not a `FAILED`. Telling a passenger their payment failed and then keeping their money is the single fastest way to destroy trust in a market that already distrusts online payment.

---

### D6 · No cashier, no counter channel

Your roles: `PASSENGER`, `AGENCY_MANAGER`, `CONTROLLER`, `ADMIN`.

Missing entirely: the person who sells most of the tickets.

```java
public enum UserRole {
    PASSENGER, CASHIER, CONTROLLER, AGENCY_MANAGER, ADMIN
}
```

And on `bookings`: `channel` (`ONLINE` | `COUNTER`), `sold_by_staff_id`. And on `payments`: `provider` must accept `CASH`.

Without this the guichet keeps its paper book, sells seat 12 while you sell seat 12, and a passenger is refused at boarding in week one. It is also what the agency owner is actually buying: *from my phone in Douala, I know what my Bafoussam counter took today.* That is a pain they feel weekly and cannot currently measure.

`sold_by_staff_id` is the column that makes cash accountability possible. It is the commercial core of the product, not an audit nicety.

---

### D7 · `Booking` holds a single `SeatNumber`

Families travel together. UC-P-03 allows 5 seats online; UC-C-04 allows 10 at the counter. One booking → many `seat_assignments`, one reference, one boarding code per seat.

---

## 3. Corrected target schema

```
agencies ──┬── agency_branches
           ├── buses ──────────────┐
           ├── routes ─────┐       │
           └── staff_users │       │
                           ▼       ▼
                    schedule_templates
                           │
                           ▼ (nightly generation, 14-day window)
                        trips
                           │
                           ▼
                  seat_assignments ◄── bookings ──► payments
                                           │            │
                                           ▼            ▼
                                    boarding_passes  refund_tasks
                                           │
                                           ▼
                                  validation_records

  cross-cutting: outbox_events · audit_logs · consent_logs · sms_log
```

New tables not in your current plan: `buses`, `staff_users`, `schedule_templates`, `trips`, `seat_assignments`, `refund_tasks`, `sms_log`.
Retired: `schedules` as currently modelled (split into templates + trips).

---

## 4. Code review, re-graded

### 4.1 · JWT role extraction — real bug, wrong diagnosis

The report calls this an **Authorization Bypass**. It isn't.

```java
String role = jwtService.extractRole(token);          // null for multi-role tokens
List.of(new SimpleGrantedAuthority("ROLE_" + role));  // → "ROLE_null"
```

`ROLE_null` matches no `@PreAuthorize` expression. The user is **denied everything**. This is a fail-closed bug — a lockout, not an escalation. It cannot grant access that wasn't there.

Why the distinction matters: a genuine authorization bypass is a stop-everything, rotate-tokens, audit-the-logs event. A lockout is a P0 functional bug you fix in the next commit. Responding to the second as though it were the first burns time you don't have; the reverse burns your users.

The suggested fix is correct — use `extractRoles()`. Add one thing the report missed: **delete the single-role overload**. Leaving both `generateAccessToken(String, String)` and `generateAccessToken(String, Set<String>)` in place guarantees someone reintroduces the mismatch. One method, one claim name, one extraction path.

**Severity: P0 functional. Not a security incident.**

### 4.2 · Outbox scheduler — real, overstated urgency

The race is real: `@Transactional` spanning the whole loop means uncommitted `SENT` markers, and a second instance would republish.

But you deploy **one instance**. The multi-instance scenario is hypothetical until you scale horizontally, which is not in this plan. The genuine present risk is smaller: a long-running listener holds a database transaction open for the duration, and connection pool exhaustion under load is a more likely failure than duplicate publication.

`SELECT ... FOR UPDATE SKIP LOCKED` is cheap and correct — do it. But do it in Sprint 6 hardening, not ahead of booking. And add the thing the report omitted: **consumers must be idempotent**. At-least-once delivery means `IssueTicketService` will eventually see the same `PaymentConfirmedEvent` twice. If it issues two tickets and sends two SMS, the outbox locking didn't save you. Idempotency belongs on the consumer side; the lock only reduces frequency.

**Severity: P2 for a single instance. P0 for consumer idempotency, which was not flagged.**

### 4.3 · `CreatedAt` clock skew — right problem, wrong fix

The report proposes:

```java
return LocalDateTime.now().plusSeconds(5).isBefore(value);   // ✗
```

This weakens a domain invariant to work around a persistence concern. Five seconds is arbitrary, it will eventually be exceeded on a badly synced VPS, and it silently permits genuinely invalid future timestamps.

The real problem is that **you are validating on reconstitution**. Invariants belong at creation. An entity loaded from the database was already valid when written; re-validating it on hydration is not a safety net, it is a crash waiting for clock drift.

```java
public record CreatedAt(Instant value) {

    // Creation: validate.
    public static CreatedAt now(Clock clock) {
        return new CreatedAt(Instant.now(clock));
    }

    public static CreatedAt of(Instant value, Clock clock) {
        if (value.isAfter(Instant.now(clock))) {
            throw new IllegalArgumentException("Timestamp is in the future");
        }
        return new CreatedAt(value);
    }

    // Reconstitution from persistence: trust the store.
    public static CreatedAt reconstitute(Instant value) {
        return new CreatedAt(value);
    }
}
```

Two benefits beyond the fix: injecting `Clock` makes every time-dependent test deterministic — which you will need constantly for hold expiry, payment timeout, and QR expiry — and moving to `Instant` resolves D4 in the same change.

**Severity: P1. Fix properly rather than with a tolerance window.**

### 4.4 · What the review missed

| Finding | Severity |
|---|---|
| Seat model cannot represent per-seat inventory (D1) | **P0** |
| `Schedule` conflates template and instance (D2) | **P0** |
| Money as `double` (D3) | **P0** |
| No consumer idempotency for outbox events | **P0** |
| No `TIMEOUT` / late-success payment handling (D5) | **P0** |
| No `CASHIER` role or counter channel (D6) | **P0** |
| `LocalDateTime` for instants (D4) | P1 |
| Booking limited to one seat (D7) | P1 |
| 17 test files vs a ≥70% coverage DoD | P1 |
| `ERROR_CODES.md` references `LIFEKORA_BLOODBANK` | P2 |
| Docs contradict on RabbitMQ (plan says yes, structure says removed, analysis says none) | P2 |
| S0-02 specifies "PostgreSQL ×5" — leftover from an abandoned microservice split | P2 |

### 4.5 · Toolchain

Java 25 toolchain, `--release 21`, Spring Boot 4, Error Prone 2.43. The 100+ warnings and the toolchain mismatch almost certainly come from Error Prone's lag behind new JDK releases — it needs `-XDcompilePolicy=simple` and a set of `--add-exports` flags for `jdk.compiler` internals, and its handling of newly-generated code shifts between JDK versions.

This is a bleeding-edge stack for a solo developer on a deadline. Two options:

- **Keep it** and pin the Error Prone/JDK combination explicitly in `libs.versions.toml`, with the exports flags in `build.gradle.kts`. Budget half a day.
- **Drop to Java 21 LTS** for the toolchain as well as `--release`. You lose nothing the MVP needs and you eliminate a category of build friction you'll otherwise hit again at every dependency bump.

I'd take the second. Nothing in the use cases requires a Java 25 feature, and build-tooling debugging is time not spent on the payment state machine.

---

## 5. The missing half: no client is planned

`JEMIL_Sprint_Plan.md` is entirely backend. S4-10 mentions a manifest "stocké localement (Android)" — there is no Android project. There is no passenger web app. `created.app` is a mockup with no backend wiring.

**Client work not in any plan:**

| Application | Use cases | Est. |
|---|---|---|
| Passenger PWA (search → seats → payment → ticket) | UC-P-01 … UC-P-11 | ~22 d |
| Counter app (cashier) | UC-C-01 … UC-C-07 | ~15 d |
| Controller PWA (offline QR + code, sync) | UC-T-01 … UC-T-08 | ~20 d |
| Agency day screen | UC-A-01, UC-A-02 | ~6 d |
| **Total** | | **~63 d** |

Roughly **13 additional weeks solo**. Realistic time to "first ticket sold in real conditions": **26–30 weeks**, not 11–14.

Three ways forward:

**A · Sequence it, accept the date.** Finish the backend, then build clients. ~7 months. Honest, and viable if you're not racing anyone.

**B · Cut the controller app.** Board from a printed manifest for the first month — exactly what agencies do today. Saves ~20 days of client work plus most of Sprint 4's offline complexity. Add the scanner in month two once tickets are actually selling. **This is the cut I'd make.**

**C · Bring in a frontend developer.** The API contracts already exist as OpenAPI specs, so the interface is defined and the work parallelises cleanly. This is where money buys the most time.

What I would not cut is counter sales. Removing them reintroduces split inventory and removes the agency's reason to adopt.

---

## 6. Revised sprint plan

Changes: a corrective sprint before booking; counter sales added; late-payment handling added; controller work moved behind the launch gate.

### S1.5 · Model correction — **new, 1 week**

| ID | Task | Defect |
|---|---|---|
| S1.5-01 | Split `schedules` into `schedule_templates` + `trips`; Liquibase migration | D2 |
| S1.5-02 | `seat_assignments` table + unique partial index | D1 |
| S1.5-03 | Remove `@Version` from `Schedule`; make `availableSeats` derived | D1 |
| S1.5-04 | `Money` value object; migrate `price` to `INTEGER` XAF | D3 |
| S1.5-05 | `Instant` everywhere; inject `Clock`; `CreatedAt.reconstitute()` | D4 |
| S1.5-06 | `buses` and `staff_users` tables | D6 |
| S1.5-07 | Add `CASHIER` to `UserRole`; delete the single-role JWT overload | D6, §4.1 |
| S1.5-08 | Nightly trip generation job, 14-day window (UC-S-06) | D2 |
| S1.5-09 | Purge `Demo` scaffolds from booking, payment, ticket, validation | — |

*Exit:* trips are searchable, seats individually assignable, the 20-parallel-request test passes.

### S2 · Booking & seat holds — 2 weeks

Keep S2-01, S2-02, S2-05 … S2-10. **Replace S2-03** (optimistic locking) with constraint-based conflict detection. **Amend S2-04** — hold TTL 10 min, expiry job every 60 s, and it must never release a hold with a payment in `INITIATED` or `PENDING`.

Maps to UC-P-01 … UC-P-04, UC-S-01, UC-S-06.

### S3 · Payment — 3 weeks

Keep the existing tickets. **Add:**

| ID | Task | UC |
|---|---|---|
| S3-22 | `TIMEOUT` state + 120 s server-side timeout | UC-P-07 |
| S3-23 | Late-success handler: reassign seat or create refund task | UC-S-03 |
| S3-24 | `refund_tasks` table + operator digest | UC-O-02 |
| S3-25 | Nightly reconciliation with three-bucket diff | UC-S-04 |
| S3-26 | Consumer-side idempotency for outbox events | §4.2 |
| S3-27 | Failure-code → French message mapping | UC-P-06 |

### S4 · Ticket delivery — 1.5 weeks *(reduced)*

Keep S4-01 … S4-07, S4-12. **Move S4-08, S4-09, S4-10, S4-13 (offline validation) to S7.**

**Add:** SMS queue with retry and cost tracking (UC-S-05); GSM-7 segment-count enforcement — one stray accented character doubles your SMS cost on every ticket.

### S5 · Counter — **new, 2 weeks**

| ID | Task | UC |
|---|---|---|
| S5-01 | Staff PIN auth, 12 h token, agency isolation | UC-C-01 |
| S5-02 | `GET /counter/trips` | UC-C-02 |
| S5-03 | `POST /counter/bookings` — cash, immediate paid | UC-C-03 |
| S5-04 | Multi-seat counter booking, cap 10 | UC-C-04 |
| S5-05 | Cancellation with manager PIN | UC-C-05 |
| S5-06 | Booking search + code resend | UC-C-06 |
| S5-07 | Per-cashier daily totals | UC-C-07 |
| S5-08 | **Cross-agency access test expecting 403** | UC-C-01 AC-5 |

### S6 · Agency & production readiness — 2 weeks

Your existing S5 dashboard tickets, reduced to **one screen** (UC-A-01) plus manifest PDF (UC-A-02) plus departure cancellation (UC-A-04). Then your S6 security and compliance tickets.

Drop for now: occupancy analytics, revenue charts, CSV export, CQRS read models. With two agencies these are SQL queries you run on Fridays.

### S7 · Controller & offline — 2.5 weeks, **after first ticket sold**

The relocated S4 offline tickets, plus UC-T-01 … UC-T-08.

### Revised totals

| Phase | Weeks |
|---|---|
| S1.5 model correction | 1 |
| S2 booking | 2 |
| S3 payment | 3 |
| S4 ticket delivery | 1.5 |
| S5 counter | 2 |
| S6 agency + production | 2 |
| **Backend to launchable** | **11.5** |
| Client applications (option B, no controller app) | ~9 |
| **To first real ticket** | **~20 weeks solo** |
| S7 controller, after launch | 2.5 |

---

## 7. Ticket traceability

| Sprint ticket | Use case | Note |
|---|---|---|
| S1-08 `GET /routes/search` | UC-P-01 | Must return trips, not schedules |
| S2-01, S2-06 | UC-P-03 | One booking → many seats |
| S2-03 | UC-P-03 AC-6 | Replace `@Version` with the unique index |
| S2-04 | UC-S-01 | Must skip holds with in-flight payments |
| S3-04, S3-07 | UC-P-05 | Write the payment row *before* calling MoMo |
| S3-05 | UC-P-05 BR-3 | New idempotency key per retry, never reused |
| S3-08, S3-09 | UC-S-02 | Always return 200; idempotent on the key |
| S3-11 | UC-S-04 | Extend to the three-bucket diff |
| — | **UC-S-03** | **No ticket exists. Add S3-23.** |
| S4-03, S4-04 | UC-P-08 | Benchmark bcrypt cost against UC-T-04's 1 s budget |
| S4-05 | UC-S-05 | Queue with retry, not inline |
| S4-08 … S4-10 | UC-T-02, UC-T-06 | Move to S7 |
| S5-01 | UC-A-02 | |
| S5-02, S5-03 | — | Defer; SQL query suffices at pilot volume |
| S6-01 … S6-08 | Global DoD | Keep |
| S6-12 load test | UC-P-03 AC-6 | Pull forward to S1.5 |
| — | **UC-C-01 … UC-C-07** | **No tickets exist. Add S5.** |

---

## 8. This week

1. **Revoke the ElevenLabs API key** pasted into chat. Then audit whether MoMo or SMS credentials have been exposed the same way, and confirm `.env` is gitignored and was never committed. `git log -p --all -S 'sk_'` will tell you.
2. **Fix the JWT role extraction** and delete the single-role overload. One commit.
3. **Write the S1.5 migration** — trips, seat assignments, unique index — before any booking code.
4. **Write the 20-parallel-request test first.** Watch it fail against the current model. That failure is the argument for S1.5, and having seen it you will never be tempted to skip the index.
5. **Decide on clients.** Option B (no controller app at launch) plus a frontend developer if funds allow. This decision governs your launch date more than any technical choice in this document.
6. **Benchmark bcrypt on a real low-end Android** against UC-T-04's 1-second budget for 70 hashes. If it fails, you need a keyed HMAC instead — and that changes the boarding-pass schema, so you want to know now, not in S7.

---

## 9. What is genuinely good here

Worth stating plainly, because the corrections above are dense and it would be easy to read them as a negative verdict.

The hexagonal boundaries are real and ArchUnit-enforced, which is rarer than it should be. Contract-first OpenAPI means your API is specified before it is built — that is exactly what makes bringing in a frontend developer viable. The outbox pattern is the right call over raw `ApplicationEvent`. Separating JPA entities from domain models is the discipline most teams abandon under deadline pressure, and you kept it. The agency module is a genuine reference implementation, and every module after it will be faster because of it.

The defects in §2 are foundation defects, which is precisely why they are worth a week now. Every one of them is cheap today and expensive in six weeks. None of them indicate the architecture is wrong — only that three entities were modelled before the product's behaviour was fully specified, which is the ordinary way this happens.
