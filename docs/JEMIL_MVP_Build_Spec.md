# JEMIL — MVP Build Specification & Workflow

**Version:** 1.0 · **Date:** 11 August 2026
**Scope:** One corridor (Douala ↔ Yaoundé), one to two partner agencies, scheduled departures only.
**Target:** First real paid ticket from a real passenger within 10 weeks.

---

## 0. Scoping decisions (read this first)

These are the decisions the rest of the document assumes. Change them consciously, not by drift.

| #  | Decision                                                                | Rationale                                                                                                                   |
|----|-------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| D1 | **Scheduled departures only.** No fill-and-go modelling.                | A reserved seat is meaningless without a departure time. Launch on VIP/premium classes, which already run to schedule.      |
| D2 | **Counter sales are in the MVP.** The caissier sells through JEMIL.     | Without this, online and counter inventory diverge and passengers get refused at boarding.                                  |
| D3 | **MTN MoMo only.** Orange Money after MVP-1 is validated.               | Two payment integrations is two sets of edge cases before you know anyone will book.                                        |
| D4 | **No accounts, no passwords for passengers.** Phone number is identity. | Removes a signup funnel step in a market with low trust. Lookup = reference + phone.                                        |
| D5 | **French is the default locale.** English is the toggle.                | The corridor is francophone. The current prototype loads in English.                                                        |
| D6 | **Monolith.** One repo, one Postgres, one deploy.                       | Microservices at 200 tickets/month costs weeks and buys nothing.                                                            |
| D7 | **Controller app is a PWA, not native Android.**                        | Camera QR + IndexedDB ships in ~1 week vs ~3 for native + SQLite sync. Go native in Phase 2 if the PWA proves insufficient. |
| D8 | **Keep the created.app site as marketing.** Do not rebuild it.          | Point its "Book Now" at the real app. Preserves the work and the demo asset.                                                |

---

## 1. What is being built

Exactly three applications plus one screen.

### A. Passenger booking app (PWA, mobile-first)
Search → trip list → seat selection → payment → ticket.

### B. Counter app (caissier, tablet/desktop web)
Same seat map, cash sales, walk-in passengers. **This is the adoption lever.**

### C. Controller app (PWA, offline-capable)
Download manifest → validate code → mark boarded → sync.

### D. Agency day screen (one page)
Today's trips, seats sold, revenue total, manifest link. Nothing else.

---

## 2. What is NOT being built

Print this list. Anything requested during the build that appears here goes to the backlog without discussion.

**Not in MVP:** GPS tracking (any level above counter taps) · driver app · parcel/colis module · diaspora payments (Stripe/PayPal/EUR) · ticket resale marketplace · agency trust score · passenger ratings · analytics dashboards · charts of any kind · platform admin backoffice · agency self-signup · incident/refund workflow module · in-app messaging · loyalty/wallet · multi-currency · Orange Money · card payments · email (SMS only) · promo codes · seat class upselling · push notifications.

**Substitutes for the above during MVP:**
- Platform admin → direct Postgres access or Metabase, read-only, for the founder.
- Incidents/refunds → WhatsApp + a manual MoMo transfer + a row in a spreadsheet.
- Agency onboarding → you configure their account by hand. Two agencies. It takes an hour.
- Analytics → one SQL query you run on Fridays.

---

## 3. Data model

PostgreSQL. Money is stored as `integer` XAF (no decimals in XAF). All timestamps `timestamptz`, stored UTC, displayed Africa/Douala.

```sql
-- Reference data (configured by hand for MVP)

agency (
  id uuid pk, name text, city text, phone_msisdn text,
  license_no text, commission_rate numeric default 0,  -- 0 during founder period
  payout_msisdn text, status text  -- active | suspended
)

route (
  id uuid pk, origin_city text, dest_city text,
  distance_km int, typical_duration_min int
)

bus (
  id uuid pk, agency_id fk, label text, plate text,
  seat_count int, seat_layout text  -- e.g. '2+2' ; keep it simple
)

staff_user (
  id uuid pk, agency_id fk, full_name text, msisdn text unique,
  role text,           -- cashier | controller | manager
  pin_hash text, status text
)

-- Operational

trip (
  id uuid pk, agency_id fk, route_id fk, bus_id fk,
  departure_at timestamptz, price_xaf int, class text,   -- standard | vip
  status text,   -- scheduled | boarding | departed | arrived | cancelled
  seats_total int, seats_sold int   -- denormalised counter, maintained in tx
)

booking (
  id uuid pk, ref text unique,          -- e.g. JML-8F3K2 (see §6)
  trip_id fk, channel text,             -- online | counter
  passenger_name text, passenger_msisdn text,
  amount_xaf int, status text,          -- held | pending_payment | paid
                                        -- | expired | cancelled | refunded
  hold_expires_at timestamptz,
  sold_by_staff_id fk null,             -- set for counter sales
  created_at timestamptz
)

seat_assignment (
  id uuid pk, trip_id fk, seat_no int, booking_id fk,
  status text                            -- held | sold
)
-- CRITICAL: this constraint is what prevents double-selling.
CREATE UNIQUE INDEX seat_once_per_trip
  ON seat_assignment (trip_id, seat_no)
  WHERE status IN ('held','sold');

payment (
  id uuid pk, booking_id fk,
  provider text,                -- mtn_momo | cash
  provider_ref text,            -- MoMo financialTransactionId
  idempotency_key text unique,  -- our X-Reference-Id sent to MoMo
  amount_xaf int,
  status text,                  -- initiated | pending | succeeded
                                -- | failed | timeout | reconciled_late
  payer_msisdn text,
  raw_response jsonb, raw_callback jsonb,
  created_at timestamptz, resolved_at timestamptz
)

boarding_pass (
  booking_id fk pk,
  code_hash text,        -- bcrypt of the 6-digit code; never store plaintext
  qr_token text,         -- signed JWT, see §6
  boarded_at timestamptz null, boarded_by fk null,
  device_id text null, sync_batch_id uuid null
)

sms_log (
  id uuid pk, msisdn text, template text, body text,
  provider_ref text, status text, cost_xaf int, created_at timestamptz
)

audit_log (
  id uuid pk, actor_type text, actor_id uuid null,
  action text, entity text, entity_id uuid,
  before jsonb, after jsonb, created_at timestamptz
)
```

**Why `seat_assignment` is a separate table and not a column on `booking`:** the unique partial index is the only thing standing between you and a double-booked seat under concurrency. A JSON array of seat numbers on `booking` cannot be constrained by the database, and application-level checks lose races.

---

## 4. Payment state machine

This is the highest-risk part of the build. Implement it before anything cosmetic.

```
                 seat held (TTL 10 min)
                          │
             POST /bookings/:id/pay
                          │
                   ┌──────▼──────┐
                   │  initiated  │  our idempotency key generated
                   └──────┬──────┘
              MoMo RequestToPay accepted (202)
                          │
                   ┌──────▼──────┐
                   │   pending   │  passenger sees USSD prompt on phone
                   └──┬───┬───┬──┘
        callback OK ───┘   │   └─── poll loop (every 3s, max 120s)
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼────┐        ┌────▼────┐        ┌────▼────┐
   │succeeded│        │ failed  │        │ timeout │
   └────┬────┘        └────┬────┘        └────┬────┘
        │                  │                  │
  seat → sold        seat released      seat released
  ref + code SMS     retry offered      "we'll SMS you if it
  status = paid      status = held      completes" + late handler
                     (if TTL remains)
```

### Non-negotiable requirements

1. **Idempotency.** Generate an `X-Reference-Id` UUID per payment attempt, store it before calling MoMo, and use it as the primary dedupe key. MoMo callbacks arrive more than once. Processing a callback twice must be a no-op.

2. **Seat hold with TTL.** The seat is `held` from the moment the passenger clicks Continue. Default TTL 10 minutes. A background job (every 60s) expires stale holds and releases seats. Show the countdown in the UI.

3. **Late success handler.** A payment can succeed after your 120s timeout. On late callback:
   - If the seat is still free → confirm the booking, send the ticket SMS, done.
   - If the seat has since been sold → auto-initiate a refund (MoMo disbursement or manual), SMS the passenger, log it as `reconciled_late`. **Do not silently keep the money.**

4. **Daily reconciliation job.** Every night, pull MoMo's transaction list for the day and diff it against your `payment` table. Alert on any mismatch. Run this from day one, not after the first incident.

5. **Never trust the client.** The amount charged is derived server-side from `trip.price_xaf × seats`, never from a request body.

### Acceptance test for this section
> 30 consecutive sandbox transactions — including 5 deliberate failures, 5 deliberate timeouts, and 3 duplicate callbacks — produce zero orphaned bookings, zero double-sold seats, and zero un-refunded late successes.

Do not proceed to Sprint 3 until this passes.

---

## 5. API surface

REST, JSON, versioned under `/api/v1`.

### Public (no auth)
```
GET  /trips?from=DLA&to=YDE&date=2026-08-14
       → [{ id, agency{name}, departure_at, arrival_est, price_xaf,
             class, seats_available }]

GET  /trips/:id/seats
       → { seat_count, layout, taken: [2,5,7,12,...] }

POST /bookings
       body { trip_id, seat_nos: [12], passenger_name, passenger_msisdn }
       → 201 { booking_id, ref, amount_xaf, hold_expires_at }
       → 409 if any seat is no longer free (return fresh seat map)

POST /bookings/:id/pay
       body { payer_msisdn }
       → 202 { payment_id, poll_after_ms: 3000 }

GET  /payments/:id
       → { status, booking_status, message_fr, message_en }

GET  /bookings/lookup?ref=JML-8F3K2&msisdn=237650000000
       → ticket detail (rate-limited: 5/min per IP)
```

### Webhooks
```
POST /webhooks/momo        -- idempotent, signature-verified, always 200
```

### Counter (staff auth: msisdn + PIN → short-lived JWT)
```
GET  /counter/trips/today          → today's departures for this agency
GET  /counter/trips/:id/seats
POST /counter/bookings             → cash sale; creates booking status=paid,
                                     payment provider=cash, prints/SMSs code
POST /counter/bookings/:id/cancel  → manager PIN required
```

### Controller (staff auth)
```
GET  /controller/trips/today
GET  /controller/trips/:id/manifest
       → { trip, passengers:[{ ref, name, seat_no, code_hash }],
           signature, issued_at, expires_at }
POST /controller/boardings/batch
       body { trip_id, device_id, events:[{ ref, boarded_at }] }
       → { accepted:[...], duplicates:[...] }
```

### Agency
```
GET  /agency/today
       → { trips:[{ route, departure_at, sold, capacity, revenue_xaf }],
           totals:{ tickets, revenue_xaf, cash, momo } }
GET  /agency/trips/:id/manifest.pdf
```

---

## 6. Ticket reference, boarding code, and QR

Three distinct artefacts. Do not conflate them.

| Artefact | Format | Purpose | Secret? |
|---|---|---|---|
| **Booking reference** | `JML-8F3K2` (3 + 5, no ambiguous chars: no 0/O/1/I) | Spoken aloud, typed into Track, quoted to support | No |
| **Boarding code** | 6 digits | What the controller validates. Works with no smartphone, no battery, no internet | **Yes** — store bcrypt hash only |
| **QR token** | Signed JWT | Fast scan path. Payload `{ booking_id, trip_id, seat_no, exp }`, signed HS256, `exp` = departure + 6h | Self-verifying |

**Offline validation logic on the controller device:**
- Manifest download includes each passenger's `code_hash`.
- Controller scans QR → verify signature locally against the embedded public key → check `booking_id` appears in the manifest.
- Or controller types the 6-digit code → bcrypt-compare against the manifest's hashes.
- Either path marks `boarded_at` locally in IndexedDB and shows a green screen.
- On sync, **first boarding wins**; any subsequent boarding for the same ref is returned as a duplicate and flagged for the agency.

**SMS the passenger receives (single message, French default):**
```
JEMIL - Reservation confirmee
JML-8F3K2 | Siege 12
General Express DLA>YDE
Ven 14 aout, 06h00
Code d'embarquement: 483920
Presentez ce code au controleur.
Aide: +237 6XX XXX XXX
```
Keep it under 160 GSM-7 characters where possible — every segment costs money at 10 FCFA.

---

## 7. Sprint plan

Each sprint ends in something deployed and demonstrable. No sprint ends in "the backend is ready."

### Sprint 0 — Non-code, starts today, runs in parallel with everything
These are the longest-lead items and they are not on your keyboard.

- [ ] **Register the company (SARL).** Prerequisite for the MoMo merchant contract.
- [ ] **Apply for MTN MoMo Collections production access.** Sandbox is 2 days; the production merchant agreement is weeks to months. This is the critical path.
- [ ] **Integrate a local aggregator as fallback** (Campay, MeSomb, Notch Pay, Flutterwave). Higher fee, faster approval. Losing 0.5% margin beats losing three months.
- [ ] **SMS account + sender ID** (Africa's Talking or a local reseller). Sender ID registration also takes time.
- [ ] **Sign one agency letter of intent.** Use the created.app prototype as the demo — it is already good enough for this.
- [ ] **Lawyer:** platform-vs-transporteur status, CGU, loi 2024/017 compliance path.

### Sprint 1 — Inventory (weeks 1–2)
Schema, trip search, seat map, seat hold. **No payment yet.**

*Done when:* two browsers attempt the same seat simultaneously and exactly one succeeds; the loser sees a fresh seat map with a clear message; holds expire and release automatically.

### Sprint 2 — Payment (weeks 3–4)
MTN MoMo sandbox end to end: initiate, poll, callback, timeout, retry, late-success, reconciliation job.

*Done when:* the 30-transaction acceptance test in §4 passes.

### Sprint 3 — Ticket delivery (week 5)
Reference generation, boarding code, QR token, SMS dispatch, lookup endpoint, Track page (status from counter taps only — **no GPS**).

*Done when:* a real phone in Douala receives a correct SMS within 30 seconds of payment, and the reference resolves on the Track page.

### Sprint 4 — Counter app (weeks 6–7)
Cash sales through the same inventory. Staff PIN login. Manager-only cancellation. Manifest print/PDF.

*Done when:* a caissier who has never seen the system sells three tickets without assistance, and those seats immediately disappear from the public seat map.

### Sprint 5 — Controller app (weeks 8–9)
Offline PWA: manifest download, QR scan, manual code entry, local boarding marks, deferred sync, duplicate detection.

*Done when:* airplane mode is enabled, 20 passengers are validated, the device reconnects, and all 20 sync with zero duplicates and zero losses.

### Sprint 6 — Launch (week 10)
Agency day screen. One agency, one route, one daily departure. Founder present at the gare.

*Done when:* 100 tickets sold in the first month, zero passengers refused at boarding, one video testimonial recorded.

---

## 8. Sequencing note: why counter comes before controller

Your v2 document orders the MVP as scanner (MVP-4) then dashboard (MVP-5), with no counter app at all. I've inverted this deliberately.

**On day one you can launch without a scanner.** The controller reads a printed manifest and ticks names, exactly as today. It is unglamorous and it works.

**You cannot launch without correct inventory.** If the counter sells outside the system, you will double-book, and a passenger refused at boarding in front of a queue is the story that ends your pilot.

Counter sales are also the agency's *reason to adopt*. Not "we bring you passengers" — they don't believe that from an unknown platform, and they're right not to. The pitch is: *from your phone in Douala, you will know exactly what your Bafoussam counter took today.* Revenue leakage between the caissier and the till is a pain every agency owner feels weekly and none of them can currently measure. That is what you are selling, and the counter app is what delivers it.

---

## 9. Tech stack

Boring on purpose.

| Layer      | Choice                                  | Note                                                                                         |
|------------|-----------------------------------------|----------------------------------------------------------------------------------------------|
| Frontend   | Next.js (React), Tailwind               | One codebase, three routes: `/`, `/counter`, `/scan`. Server-render the public pages for 2G. |
| Backend    | Node + Express or Next API routes       | Same repo. No separate service.                                                              |
| DB         | PostgreSQL 16                           | The unique partial index in §3 is load-bearing.                                              |
| Queue/cron | `pg-boss` or plain `node-cron`          | For hold expiry, SMS retry, nightly reconciliation.                                          |
| Offline    | IndexedDB via `idb`, service worker     | Controller PWA only.                                                                         |
| Hosting    | Single VPS (Contabo/Hetzner) or Railway | See §10 on data residency.                                                                   |
| Errors     | Sentry (free tier)                      |                                                                                              |
| Logs       | `pino` → file → weekly review           |                                                                                              |

**Performance budget for the passenger app** (this market, not your laptop):
- First contentful paint under 3s on simulated 3G
- Initial JS bundle under 150 KB gzipped
- No WebGL, no 3D, no video on the booking path
- Every image WebP, lazy-loaded, under 60 KB
- The booking flow must be usable with images fully blocked

The created.app site can keep its 3D. The booking app cannot.

---

## 10. Compliance items that affect the build

Not lawyer work — engineering work with deadlines.

1. **Loi n°2024/017 takes effect 23 June 2026.** That date has passed. You are building into an enforced regime, not a transition period. Confirm current enforcement posture with your lawyer before launch.

2. **Data residency.** Your v2 document says "AWS Africa (Cape Town) ou OVH pour éviter transfert hors Cameroun." Cape Town is South Africa — that *is* a transfer outside Cameroon. Either host in Cameroon or obtain the transfer authorisation, and either way stop promising *"vos données restent au Cameroun"* until it's true. Your own §7.2 warns against unverifiable promises; this is one.

3. **Consent capture.** Explicit unchecked checkbox at booking, stored with timestamp, IP, and the version of the policy text accepted. Build it in Sprint 1 — retrofitting consent records is painful.

4. **PII in logs.** MSISDNs must be masked in application logs (`+2376XXXXX53`). Raw MoMo payloads go in `payment.raw_callback`, not stdout.

5. **Settlement to agencies within 48h.** Manual bank/MoMo transfer is fine for two agencies. Log every transfer in `audit_log` with the reference. Do not automate this in MVP.

6. **TVA 19.25% on commissions.** During the zero-commission founder period there is nothing to invoice. Set up invoicing before the first commission is charged, not after.

---

## 11. Metrics — the only four that matter in month one

Ignore GMV. Ignore registered users. Ignore anything with a chart.

| Metric                                        | Target          | Why                                                                                                            |
|-----------------------------------------------|-----------------|----------------------------------------------------------------------------------------------------------------|
| **JEMIL share of the agency's daily tickets** | >25% by week 4  | 5% means you're a novelty. 40% means you're infrastructure. This is the single health metric.                  |
| Payment success rate                          | >92%            | Below this, your MoMo integration or your UX is broken. Investigate every failure individually at this volume. |
| Passengers refused at boarding                | **0**           | Non-negotiable. Any occurrence is a P0 incident.                                                               |
| Support contacts per 100 tickets              | <10 and falling | Rising means the product is confusing, not that people like talking to you.                                    |

---

## 12. Immediate next actions

In order, this week:

1. Start the MTN MoMo merchant application and company registration **today** — everything else can proceed in parallel, this cannot be compressed later.
2. Freeze scope against §1 and §2. Write the not-building list somewhere you'll see it daily.
3. Confirm whether you are building this yourself or hiring. Neither founding document states this, and it is the largest unknown in the entire plan. If hiring: budget the mid scenario (~9.5M FCFA) and expect 14–18 weeks, not 10–14.
4. Stand up the schema in §3, including the unique partial index, and write the concurrency test before writing any UI.
5. Book the agency demo using the existing created.app prototype. It is ready for that purpose now.
