# JEMIL Backend — Progress Status

**Last updated:** 2026-09-04  
**Branch:** `working/JEMIL-7`  
**Owner:** update this file whenever a sprint slice lands or a P0 UC moves.

---

## Sprint board

| Phase | Goal | Status | Notes |
|-------|------|--------|-------|
| **S0** Foundation | Hexagonal monolith, Liquibase, JWT, ArchUnit, OpenAPI | Done | |
| **S1** Agency | CRUD agencies, routes, branches, cities, search, suspend | Done | Cucumber agency 9/9 |
| **S1.5** Model correction | Inventory schema, XAF int, seat concurrency, trip/hold APIs | **Nearly done** | See checklist below |
| **S2** Booking UX complete | Seat map, passenger details, hold expiry job real | Not started | UC-P-02, UC-P-04, UC-S-01 |
| **S3** Payment MoMo | Initiate / callback / timeout / late success | Not started | UC-P-05…07 |
| **S4** Ticket + SMS | Boarding pass, SMS queue | Not started | UC-P-08, UC-S-05 |
| **S5** Counter | Cashier PIN, cash booking | Not started | UC-C-* |
| **S6+** Launch / controller | Agency day screen, offline scan | Not started | After first ticket |

Canonical plan: [`JEMIL_Backend_Gap_Analysis.md`](JEMIL_Backend_Gap_Analysis.md) §6 (revised).

---

## S1.5 checklist

| ID | Task | Done |
|----|------|------|
| S1.5-01 | Tables `schedule_templates` + `trips` (Liquibase v8) | Yes (domain still also has legacy `schedules`) |
| S1.5-02 | `seat_assignments` + unique partial index | Yes |
| S1.5-03 | Remove schedule `@Version` | Yes |
| S1.5-04 | Integer XAF route/trip prices | Yes |
| S1.5-05 | `CreatedAt.reconstitute()` (+ Clock where injected) | Partial (`LocalDateTime` remains) |
| S1.5-06 | `buses` / `staff_users` tables | Tables yes; staff domain no |
| S1.5-07 | `CASHIER` role + multi-role JWT | Yes |
| S1.5-08 | Nightly trip generation (14-day window) | **No** |
| S1.5-09 | Purge Demo scaffolds | **No** |
| Exit | `GET /trips/search` | Yes |
| Exit | `POST /bookings` hold + 409 | Yes |
| Exit | 20-parallel seat test | Yes |
| Exit | Cucumber booking e2e | In this change |

---

## P0 use cases (MVP)

| UC | Title | Backend |
|----|-------|---------|
| UC-P-01 | Search departure | Partial — trips search API exists |
| UC-P-02 | Seat map | No |
| UC-P-03 | Place hold | Partial — API + DB concurrency |
| UC-P-04 | Passenger details | No |
| UC-P-05 | MoMo pay | No |
| UC-P-06 | Payment fail | No |
| UC-P-07 | Payment timeout | No |
| UC-P-08 | Ticket / SMS | No |
| UC-S-01…06 | System jobs | Stub / missing |
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

1. Close S1.5 leftovers: trip generation job (UC-S-06), demo purge.
2. Start S2: seat map + hold expiry that skips in-flight payments.
3. Parallel non-code: MoMo merchant + SMS provider (Build Spec §7 Sprint 0).
