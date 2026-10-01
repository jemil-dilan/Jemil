# JEMIL Backend — Progress Status

**Last updated:** 2026-09-15  
**Branch:** `chore/refactor-booking-step-restassured`  
**Owner:** update this file whenever a sprint slice lands or a P0 UC moves.

---

## Sprint board

| Phase | Goal | Status | Notes |
|-------|------|--------|-------|
| **S0** Foundation | Hexagonal monolith, Liquibase, JWT, ArchUnit, OpenAPI | Done | |
| **S1** Agency | CRUD agencies, routes, branches, cities, search, suspend | Done | Cucumber agency e2e |
| **S1.5** Model correction | Inventory, XAF, concurrency, trip/hold APIs, tripgen | **Closed** (known debts below) | Exit criteria met |
| **S2** Booking UX complete | Seat map, passenger details, hold expiry payment-aware | In progress | UC-P-02, UC-P-04; UC-S-01 core done |
| **S3** Payment MoMo | Initiate / callback / timeout / late success | Not started | UC-P-05…07 |
| **S4** Ticket + SMS | Boarding pass, SMS queue | Not started | UC-P-08, UC-S-05 |
| **S5** Counter | Cashier PIN, cash booking | Not started | UC-C-* |
| **S6+** Launch / controller | Agency day screen, offline scan | Not started | After first ticket |

Canonical plan: [`JEMIL_Backend_Gap_Analysis.md`](JEMIL_Backend_Gap_Analysis.md) §6 (revised).

---

## S1.5 checklist (verified 2026-09-15)

| ID | Task | Done |
|----|------|------|
| S1.5-01 | Tables `schedule_templates` + `trips` (Liquibase v8) | Yes — legacy `schedules` still coexists (debt) |
| S1.5-02 | `seat_assignments` + unique partial index | Yes |
| S1.5-03 | Remove schedule `@Version` | Yes |
| S1.5-04 | Integer XAF route/trip prices | Yes |
| S1.5-05 | `Instant` everywhere; `Clock`; `CreatedAt.reconstitute()` | Partial — `reconstitute()` + `Clock` on trip search; still `LocalDateTime` |
| S1.5-06 | `buses` / `staff_users` tables | Tables yes; staff domain deferred |
| S1.5-07 | `CASHIER` + multi-role JWT only | Yes |
| S1.5-08 | Nightly trip generation 14-day (Africa/Douala) | Yes — service + cron + unit + Cucumber simulated tick + seed (v11) |
| S1.5-09 | Purge Demo scaffolds | Yes — code/OpenAPI gone; v10 drops demo tables |
| Exit | `GET /trips/search` | Yes |
| Exit | `POST /bookings` hold + 409 | Yes (10 min hold) |
| Exit | 20-parallel seat test | Yes |
| Exit | Cucumber booking e2e | Yes |

### Known debts (not blocking S2)

- Legacy agency `schedules` vs MVP `trips` still both present.
- `CreatedAt` / timestamps not fully on `Instant`.
- Hold expiry skips in-flight payments (INITIATED/PENDING) deferred until payment module → **S2 / UC-S-01 AC**.
- No `staff_users` domain/API yet → counter sprint.

---

## P0 use cases (MVP)

| UC | Title | Backend |
|----|-------|---------|
| UC-P-01 | Search departure | Partial — `GET /trips/search` |
| UC-P-02 | Seat map | Yes — `GET /trips/{id}/seats` |
| UC-P-03 | Place hold | Partial — API + DB concurrency |
| UC-P-04 | Passenger details | No |
| UC-P-05 | MoMo pay | No |
| UC-P-06 | Payment fail | No |
| UC-P-07 | Payment timeout | No |
| UC-P-08 | Ticket / SMS | No |
| UC-S-01 | Hold expiry | Yes — job + Cucumber simulated tick; payment-aware skip deferred |
| UC-S-06 | Trip generation | Yes — + Cucumber simulated tick / idempotent |
| UC-C-* | Counter | No |

Full AC: [`JEMIL_Use_Cases_Acceptance_Criteria.md`](JEMIL_Use_Cases_Acceptance_Criteria.md).

---

## How to verify

```bash
./gradlew test
./gradlew e2eTest
```

---

## Next up

1. **S2** — passenger details + consent (UC-P-04); payment-aware hold expiry skip once payments land.
2. Parallel non-code: MoMo merchant + SMS provider (Build Spec §7 Sprint 0).
3. Optional: `GET /trips/{id}` trip detail if clients need sticky summary without search cache.
