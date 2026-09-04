# JEMIL MVP — Use Case Catalogue & Acceptance Criteria

**Version:** 1.0 · **Date:** 11 August 2026
**Companion to:** `JEMIL_MVP_Build_Spec.md`
**Purpose:** Every unit of work in the MVP, with criteria specific enough that "done" is not a matter of opinion.

---

## How to use this document

1. A use case is **not closed** until every one of its acceptance criteria passes *and* the Global Definition of Done (§1) is satisfied.
2. Criteria are written to be checkable by someone who did not write the code. If a criterion can be argued about, it is badly written — rewrite it, don't skip it.
3. Work P0 → P1 → P2. Never start a lower tier while a higher tier is incomplete.
4. When you cut scope under time pressure, cut whole use cases from the bottom. Never cut acceptance criteria from a use case you are keeping.

### Priority tiers

| Tier   | Meaning                                                                 | Count |
|--------|-------------------------------------------------------------------------|-------|
| **P0** | No ticket can be sold without it. The product does not exist.           | 14    |
| **P1** | Launch is unsafe, or the agency will not adopt, without it.             | 12    |
| **P2** | Needed before public launch, but can be done manually during the pilot. | 7     |
| **P3** | Backlog. Explicitly not in MVP.                                         | —     |

### Actors

| Code | Actor                                            |
|------|--------------------------------------------------|
| PAS  | Passenger (no account; phone number is identity) |
| CAI  | Cashier / caissier at the agency counter         |
| CTR  | Controller / contrôleur at boarding              |
| MGR  | Agency manager                                   |
| OPS  | Platform operator (you)                          |
| SYS  | Background job / system process                  |
| EXT  | External service (MTN MoMo, SMS provider)        |

---

## 1. Global Definition of Done

Applies to **every** use case. A use case with all its specific criteria passing but any of these failing is **not done**.

| #  | Criterion                                                                                                                            |
|----|--------------------------------------------------------------------------------------------------------------------------------------|
| G1 | Works on a 4-year-old Android device, Chrome, 360 px viewport width.                                                                 |
| G2 | All user-facing text exists in French and English. **French is the default.** No untranslated string reaches the user.               |
| G3 | Every error path displays a human-readable message in the user's language. No raw stack traces, no "Error 500", no silent failures.  |
| G4 | Server validates all input. Prices, amounts, trip IDs and seat availability are derived server-side and never taken from the client. |
| G5 | Phone numbers are masked in all application logs (`+2376XXXXX53`).                                                                   |
| G6 | The happy path and at least one failure path have an automated test that runs in CI.                                                 |
| G7 | Any state change touching money or seat inventory writes an `audit_log` row (actor, action, before, after).                          |
| G8 | Manually executed once on a real phone, on a real Cameroonian mobile network, before the use case is closed.                         |
| G9 | No new dependency added without a one-line justification in the commit message.                                                      |

---

## 2. Passenger — booking (UC-P)

### UC-P-01 · Search for a departure · **P0** · PAS

**Precondition:** At least one trip exists with `status = scheduled` and a future departure.

**Main flow:** Passenger opens the site → selects origin city, destination city, and date → taps *Rechercher* → sees the list of matching departures.

**Acceptance criteria**

1. A search with valid origin, destination and date returns only trips with `status = scheduled` whose `departure_at` is more than 30 minutes in the future.
2. Each result displays: agency name, departure time, estimated arrival time, price, class badge (Standard / VIP), and seats remaining.
3. A trip with zero seats remaining appears in the list marked **Complet** and is not selectable.
4. A date in the past is rejected on both client and server; the server returns 400, not an empty list.
5. A route with no departures on the chosen date shows an empty state that names the next date with availability — never a blank screen.
6. Results render in under 3 seconds on Chrome DevTools "Slow 3G" throttling.
7. Prices display as `5 000 FCFA` (narrow no-break space, FCFA not XAF). Verify with a Cameroonian reader, not a developer.
8. The search form retains the passenger's selections after a failed search — they do not re-enter everything.

---

### UC-P-02 · View seat availability · **P0** · PAS

**Main flow:** Passenger selects a departure → sees the seat map with taken seats marked.

**Acceptance criteria**

1. The seat map renders the bus's actual layout from `bus.seat_layout` and `bus.seat_count`. A 70-seat bus does not render as 40 seats.
2. Seats already `held` or `sold` are visually distinct (different fill and border, not colour alone — some users are colour-blind, and some screens are washed out in daylight) and are not interactive.
3. The driver position is labelled and never selectable.
4. The seat map refetches availability every 20 seconds while displayed, without losing the passenger's current selection.
5. Seat state shown to the passenger is never more than 20 seconds stale.
6. The map is fully usable at 360 px width without horizontal scrolling.

---

### UC-P-03 · Select a seat and place a hold · **P0** · PAS

> **This is the highest-risk use case in the passenger app.** Criterion 6 is the one that prevents the failure mode that would end your pilot.

**Main flow:** Passenger taps a free seat → taps *Continuer* → a hold is created → a countdown appears.

**Acceptance criteria**

1. Tapping a free seat selects it; tapping the same seat again deselects it.
2. *Continuer* is disabled until at least one seat is selected.
3. On continue, the server creates one `booking` row with `status = held` and `hold_expires_at = now() + 10 minutes`, plus one `seat_assignment` row per seat with `status = held`, in a single transaction.
4. A visible countdown shows the remaining hold time and updates every second.
5. If the countdown reaches zero, the passenger is returned to the seat map with a clear message; the hold and seat assignments are released.
6. **Concurrency test (automated, mandatory):** 20 parallel requests for the same seat on the same trip → exactly 1 returns 201, exactly 19 return 409, and the database contains exactly 1 `seat_assignment` row for that seat. This test runs in CI and must never be skipped or marked flaky.
7. On a 409, the client automatically refetches the seat map and displays *« Ce siège vient d'être pris. Choisissez-en un autre. »*
8. Refreshing the browser during an active hold restores the hold state rather than orphaning it.
9. Selecting more than 5 seats in one booking is rejected (counter staff handle group bookings).

---

### UC-P-04 · Enter passenger details · **P0** · PAS

**Acceptance criteria**

1. Required fields: full name, phone number. Email is optional and clearly marked so.
2. The phone number field is prefilled with `+237` and accepts only valid Cameroonian mobile formats.
3. Validation errors appear next to the offending field, in the user's language, before submission is possible.
4. The name field accepts accented characters, apostrophes and hyphens (`N'Guessan`, `Mbarga-Fouda`, `Théodore`).
5. Details entered are preserved if payment subsequently fails — the passenger does not retype them to retry.
6. An explicit, **unchecked-by-default** consent checkbox for data processing is present; submission is blocked without it, and acceptance is stored with timestamp, IP, and policy version.

---

### UC-P-05 · Pay by MTN Mobile Money · **P0** · PAS + EXT

**Main flow:** Passenger confirms the amount → enters the paying phone number → receives a USSD prompt on their handset → enters their MoMo PIN → booking is confirmed.

**Acceptance criteria**

1. The payer's phone number defaults to the passenger's number but is editable — someone else frequently pays.
2. The payer number is validated against known MTN Cameroon prefixes before the request is sent.
3. The charged amount is computed server-side as `trip.price_xaf × seat count`. A request body containing a different amount is ignored, and the discrepancy is logged.
4. A `payment` row with a unique `idempotency_key` is written **before** the outbound call to MoMo, not after.
5. Initiating payment extends `hold_expires_at` to cover the full payment window — an in-flight payment can never be killed by hold expiry.
6. The UI enters a pending state instructing the passenger to check their phone and enter their MoMo code, with a visible progress indicator.
7. The client polls payment status every 3 seconds.
8. The pay button is disabled on submit **and** the server rejects a duplicate submission with the same idempotency key.
9. On success: `booking.status = paid`, all seat assignments → `sold`, ticket SMS queued, and the confirmation screen appears within 2 seconds of the callback arriving.
10. The passenger is never shown a confirmation screen before the payment has actually succeeded server-side.

---

### UC-P-06 · Payment fails · **P0** · PAS + EXT

**Acceptance criteria**

1. Failure reasons are mapped to human messages in French: insufficient balance, wrong PIN, request rejected, account inactive. Never surface the raw provider error code to the passenger.
2. The seat is released and the seat map refreshed.
3. If hold time remains, the passenger can retry immediately without re-entering their details; if not, they return to the seat map.
4. A retry creates a **new** payment row with a **new** idempotency key. Retries are never sent with a reused key.
5. Three consecutive failures display the WhatsApp support number.
6. Test: each distinct failure code from the MoMo sandbox produces the correct French message and the correct seat state.

---

### UC-P-07 · Payment times out · **P0** · PAS + EXT

**Acceptance criteria**

1. After 120 seconds with no resolution, `payment.status = timeout`.
2. The passenger sees a message that does **not** claim failure — because the payment may still succeed: *« Le paiement n'a pas abouti dans le délai. Si votre compte a été débité, vous recevrez votre billet par SMS. Sinon, aucun montant ne sera prélevé. »*
3. The seat is released.
4. The WhatsApp support number is displayed.
5. Any later callback for this payment is handled by UC-S-03 and is **not** rejected as stale.

---

### UC-P-08 · Receive the ticket · **P0** · PAS + EXT

**Acceptance criteria**

1. A confirmation SMS is dispatched within 30 seconds of payment success.
2. The SMS contains: booking reference, seat number, agency, route, date and departure time, 6-digit boarding code, support number.
3. The SMS is in French and fits within a single 160-character GSM-7 segment wherever possible. Segment count is measured, not assumed.
4. The confirmation screen shows the same information, plus a QR code.
5. The 6-digit boarding code is stored as a bcrypt hash. The plaintext exists only in the outbound SMS and is never written to logs or the database.
6. Test: send to a real MTN number and a real Orange number in Cameroon; confirm delivery and legibility on a basic feature phone, not only a smartphone.

---

### UC-P-09 · Look up an existing ticket · **P1** · PAS

**Acceptance criteria**

1. A booking reference plus the passenger's phone number returns the ticket; reference alone does not.
2. A wrong combination returns a generic "not found" that does not reveal whether the reference exists.
3. The endpoint is rate-limited to 5 requests per minute per IP.
4. The ticket view shows current trip status: *Programmé*, *Embarquement*, *Parti*, *Arrivé*, or *Annulé*.
5. Lookup works for tickets sold at the counter, not only online.

---

### UC-P-10 · Abandon a booking · **P1** · PAS + SYS

**Acceptance criteria**

1. Closing the browser during a hold leaves the seat locked for no longer than the remaining TTL.
2. The expiry job (UC-S-01) releases it, and the seat is bookable again by another passenger within 60 seconds of expiry.
3. No orphaned `booking` rows in `held` state older than 15 minutes exist at any time. Verified by a query run nightly.

---

### UC-P-11 · Switch language · **P2** · PAS

**Acceptance criteria**

1. The site loads in French by default regardless of browser locale.
2. The FR/EN toggle switches all interface text, including error messages and SMS templates.
3. The chosen language persists across pages for the session.
4. Language chosen at booking determines the language of the ticket SMS.

---

## 3. Counter / cashier (UC-C)

> Without this group, online and counter inventory diverge and passengers are refused at boarding. It is not optional, and it is what makes the agency want the product.

### UC-C-01 · Cashier signs in · **P0** · CAI

**Acceptance criteria**

1. Sign-in is by phone number plus a 4-digit PIN. No email, no password.
2. A successful sign-in returns a JWT valid for 12 hours (one working day).
3. Five consecutive failed PIN attempts lock the account for 15 minutes and notify the manager.
4. The session survives a page refresh and a brief network loss.
5. A cashier can only ever see and act on their own agency's data. Verified by an automated test attempting cross-agency access with a valid token.

---

### UC-C-02 · View today's departures · **P0** · CAI

**Acceptance criteria**

1. Lists all of this agency's trips departing today, ordered by departure time.
2. Each row shows: route, departure time, seats sold, capacity, and status.
3. Departed trips are visually separated from upcoming ones.
4. The list refreshes automatically every 30 seconds.

---

### UC-C-03 · Sell a cash ticket at the counter · **P0** · CAI

**Main flow:** Cashier selects a departure → sees the same seat map the passenger sees → picks a seat → enters passenger name and phone → confirms cash received → boarding code is issued.

**Acceptance criteria**

1. The cashier sees the identical live seat map as the public app, including seats currently held online.
2. Completing a counter sale creates a `booking` with `channel = counter`, `status = paid`, and a `payment` with `provider = cash`.
3. The seat disappears from the public seat map **within 20 seconds** of the counter sale.
4. The whole flow takes **under 30 seconds** for a cashier who has done it ten times. Measure this with a stopwatch on a real cashier, not an estimate.
5. The boarding code is displayed on screen large enough to read across a counter, and sent by SMS if a phone number was supplied.
6. A sale with no phone number is permitted — some walk-in passengers have no phone. The code is then displayed and printed only.
7. `sold_by_staff_id` records which cashier made the sale.
8. The seat map remains usable when the connection drops mid-sale: the sale either completes or fails cleanly, never leaves a seat in limbo.

---

### UC-C-04 · Sell several seats in one transaction · **P1** · CAI

**Acceptance criteria**

1. The cashier can select up to 10 seats in a single counter booking.
2. One booking reference covers the group; each seat still gets its own boarding code.
3. The total is displayed clearly before confirmation.
4. If any selected seat becomes unavailable during the transaction, the whole sale is rejected with a clear message — never partially completed.

---

### UC-C-05 · Cancel a booking · **P1** · CAI + MGR

**Acceptance criteria**

1. Cancellation requires a manager PIN, not a cashier PIN.
2. A reason must be selected from a fixed list before the action proceeds.
3. The seat returns to the public map within 20 seconds.
4. The passenger receives a cancellation SMS if a phone number is on file.
5. For a MoMo-paid booking, a refund task is created — cancellation does not silently keep the passenger's money.
6. Every cancellation writes an `audit_log` entry naming the manager who authorised it.

---

### UC-C-06 · Reprint or resend a boarding code · **P1** · CAI

**Acceptance criteria**

1. The cashier can find a booking by reference, passenger phone, or passenger name.
2. Resending issues the **same** code, not a new one.
3. Resends are rate-limited to 3 per booking per hour and are logged.
4. The code is never displayed in plaintext in any list view — only after an explicit "show" action on a single booking.

---

### UC-C-07 · End-of-day totals · **P1** · CAI

**Acceptance criteria**

1. A single screen shows, for today and this cashier: tickets sold, cash total, MoMo total, and combined total.
2. Figures match the sum of the underlying `payment` rows exactly. Verified by an automated reconciliation test.
3. The screen is readable and printable on a tablet.
4. Totals are frozen at midnight Africa/Douala and remain retrievable for past days.

---

## 4. Controller / boarding (UC-T)

### UC-T-01 · Controller signs in and selects a trip · **P0** · CTR

**Acceptance criteria**

1. Sign-in is by phone number plus PIN, same mechanism as the cashier.
2. After sign-in, the controller sees only their agency's trips departing within the next 4 hours.
3. Selecting a trip triggers the manifest download (UC-T-02).

---

### UC-T-02 · Download the manifest · **P0** · CTR

**Acceptance criteria**

1. The manifest contains every paid booking for the trip: reference, passenger name, seat number, boarding code hash.
2. It is signed by the server; the client verifies the signature before use.
3. A manifest with an invalid signature is refused with a clear message and is not used for validation.
4. It is stored in IndexedDB and survives a browser restart.
5. Download completes in under 10 seconds on 3G for a 70-passenger manifest.
6. The controller can re-download to pick up bookings made after the first download, and re-downloading does not erase boardings already recorded locally.

---

### UC-T-03 · Validate by QR scan · **P0** · CTR

**Acceptance criteria**

1. The device camera opens and scans a QR displayed on a passenger's phone screen.
2. Signature verification happens **locally**, with no network call.
3. A valid, not-yet-boarded pass shows a full-screen **green** result with the passenger name and seat number, held for at least 2 seconds.
4. Scanning succeeds in bright outdoor daylight and on a cracked or dim phone screen. Test both conditions physically.
5. Median scan-to-result time is under 2 seconds.
6. An expired token (past departure + 6h) is refused.

---

### UC-T-04 · Validate by 6-digit code · **P0** · CTR

> This is the path that works when the passenger's phone is dead, has no data, or is not a smartphone. It will be used more often than you expect.

**Acceptance criteria**

1. The controller can type a 6-digit code with a large numeric keypad usable in a moving queue.
2. The entered code is bcrypt-compared against the local manifest hashes, offline.
3. A valid code produces the same green result as a QR scan.
4. An unknown code produces a full-screen **red** result reading *« Billet introuvable »*.
5. Comparison completes in under 1 second for a 70-passenger manifest on a low-end device.

---

### UC-T-05 · Reject an already-boarded or invalid pass · **P0** · CTR

**Acceptance criteria**

1. Re-presenting an already-boarded pass shows an **orange** result reading *« Déjà embarqué »* plus the original boarding time.
2. A pass for a different trip shows red and names the trip it actually belongs to.
3. A cancelled or refunded booking shows red with the reason.
4. Green, orange and red are distinguishable by shape and text as well as colour.
5. No rejection ever crashes, freezes, or leaves the scanner in an unusable state — verified by 50 consecutive rejections.

---

### UC-T-06 · Operate fully offline · **P0** · CTR

**Acceptance criteria**

1. With the device in airplane mode, the cached manifest opens and validation works for both QR and code paths.
2. 20 passengers are validated offline; all 20 are recorded locally with accurate timestamps.
3. Re-presenting a pass already validated offline correctly shows "already boarded" from local state.
4. Force-closing the browser mid-session and reopening it loses **zero** recorded boardings.
5. A visible indicator shows offline status and the count of boardings awaiting sync.
6. The device battery supports a full 70-passenger boarding session with the camera active.

---

### UC-T-07 · Sync boarding events · **P0** · CTR + SYS

**Acceptance criteria**

1. On reconnection, queued boardings upload automatically in a single batch without controller action.
2. The server returns accepted and duplicate lists; the client clears only what was accepted.
3. **First boarding wins.** If two devices boarded the same reference, the earlier timestamp is authoritative and the later is flagged for the agency.
4. A failed sync retries with exponential backoff and never silently discards events.
5. Sync is idempotent: submitting the same batch three times produces the same server state.
6. Test: two controller devices board an overlapping set offline, then both sync. No boarding is lost, and every conflict is flagged.

---

### UC-T-08 · Mark the trip as departed · **P1** · CTR

**Acceptance criteria**

1. One tap sets `trip.status = departed` with a timestamp.
2. Confirmation is required — an accidental tap does not depart a bus.
3. Passengers on this trip who look up their ticket see status *Parti*.
4. The action is queued and synced if performed offline.
5. This is the only trip-status signal in the MVP. **No GPS.**

---

## 5. Agency manager (UC-A)

### UC-A-01 · View today's operations · **P0** · MGR

> One screen. Resist every temptation to add a second.

**Acceptance criteria**

1. Shows every trip departing today: route, time, seats sold, capacity, revenue, status.
2. Shows agency totals for the day: tickets, revenue, cash split, MoMo split.
3. Figures reconcile exactly with the sum of the underlying payments.
4. Loads in under 3 seconds on 3G.
5. Refreshes automatically every 60 seconds.
6. Usable on a phone — the manager will check this from a car, not a desk.

---

### UC-A-02 · Open and export a manifest · **P0** · MGR

**Acceptance criteria**

1. Any trip's manifest is viewable: seat number, passenger name, phone, channel, boarding status.
2. Exportable as PDF, formatted for A4 printing.
3. The printed manifest is legible in black and white on a low-toner office printer. Print one and check.
4. Passenger phone numbers appear in full on the manifest (the agency legitimately needs them) but the export is watermarked with the requesting user and timestamp.

---

### UC-A-03 · Define a recurring schedule · **P0** · MGR or OPS

> **This was missing from the earlier build spec.** Without it, someone creates every trip by hand — roughly 240 records a month for two agencies. That breaks in week two.

**Acceptance criteria**

1. A schedule template specifies: route, bus, departure time, days of the week, price, class.
2. Saving a template immediately generates trips for the next 14 days.
3. A nightly job (UC-S-06) maintains the 14-day rolling window.
4. Editing a template affects only future trips with no bookings; trips that already have bookings are never mutated.
5. Deactivating a template stops future generation and leaves existing trips untouched.
6. Two templates cannot assign the same bus to overlapping departure windows — this is rejected at save time with a clear message.

---

### UC-A-04 · Cancel a departure · **P1** · MGR

**Acceptance criteria**

1. Cancellation requires a manager PIN and a reason from a fixed list.
2. All affected passengers receive an SMS within 5 minutes.
3. A refund task is created for every paid booking on the trip.
4. The trip disappears from public search immediately.
5. The manifest remains accessible after cancellation for record-keeping.
6. Every cancellation writes an `audit_log` entry.

---

## 6. System / background jobs (UC-S)

### UC-S-01 · Expire stale seat holds · **P0** · SYS

**Acceptance criteria**

1. Runs every 60 seconds.
2. Releases all `seat_assignment` rows whose booking is `held` and past `hold_expires_at`, setting the booking to `expired`.
3. Never touches a hold whose booking has a payment in `initiated` or `pending` state.
4. Idempotent — running it twice in the same minute changes nothing on the second run.
5. Logs the count released; an unusually high count triggers an alert.

---

### UC-S-02 · Process a MoMo callback · **P0** · SYS + EXT

**Acceptance criteria**

1. The endpoint always returns HTTP 200 to the provider, even on internal error — otherwise the provider retries forever.
2. Callback signature/source is verified before the payload is trusted.
3. Processing is keyed on the idempotency key. **A callback delivered three times produces exactly one state change.** This is an explicit automated test.
4. The raw payload is stored in `payment.raw_callback` regardless of outcome.
5. A callback for an unknown payment is logged and alerted, never silently discarded.
6. Median processing time is under 500 ms.

---

### UC-S-03 · Handle a late payment success · **P0** · SYS

> The scenario: the passenger's payment succeeded at T+180s, after you timed out at T+120s. You are holding their money. This use case decides whether they trust you.

**Acceptance criteria**

1. A success callback for a `timeout` payment is **accepted**, not rejected as stale.
2. If every seat from the original booking is still free: seats are reassigned, `booking.status = paid`, the ticket SMS is sent, and `payment.status = reconciled_late`.
3. If any seat has since been sold: a refund task is created, the passenger receives an explanatory SMS within 5 minutes, and an `audit_log` entry is written.
4. Under no circumstances does the money remain with JEMIL while the passenger holds no ticket.
5. Test: simulate a success callback at T+300 seconds for both branches and verify the outcome of each.

---

### UC-S-04 · Nightly payment reconciliation · **P0** · SYS + EXT

**Acceptance criteria**

1. Runs nightly, pulls the provider's transaction list for the day, diffs it against the `payment` table.
2. Reports three categories: in provider but not in JEMIL; in JEMIL but not in provider; amount mismatches.
3. Any non-empty category sends an alert to the operator — not a log line nobody reads.
4. A reconciliation report is retained for at least 12 months.
5. Runs from the very first day of live payments, not after the first incident.

---

### UC-S-05 · Dispatch SMS with retry · **P0** · SYS + EXT

**Acceptance criteria**

1. Messages are queued, not sent inline during a web request.
2. Failed sends retry 3 times with exponential backoff.
3. After 3 failures, the message is marked failed and the operator is alerted.
4. Every message is recorded in `sms_log` with provider reference, status and cost.
5. Ticket SMS are prioritised ahead of all other message types.
6. A daily SMS spend figure is available — this is a real cost centre at 10 FCFA per segment.

---

### UC-S-06 · Generate trips from schedule templates · **P0** · SYS

**Acceptance criteria**

1. Runs nightly and maintains a 14-day rolling window of generated trips.
2. Never creates a duplicate trip for an existing template/date pair. Verified by a unique constraint, not by application logic alone.
3. Skips dates where a trip already exists for that template.
4. Logs the number created; zero created on a normal night triggers an alert (it means generation is broken).

---

## 7. Platform operator (UC-O)

> Deliberately manual. With two agencies, building admin tooling is wasted effort.

### UC-O-01 · Onboard an agency · **P2** · OPS

**Acceptance criteria**

1. A documented SQL or seed script creates: agency, buses, routes, staff users with PINs, and schedule templates.
2. The full onboarding of one agency takes under one hour.
3. Staff PINs are delivered out-of-band, never by email or in the same channel as the phone number.
4. A checklist exists and has been executed end-to-end at least once before the first real agency.

---

### UC-O-02 · Issue a refund · **P2** · OPS

**Acceptance criteria**

1. A documented procedure covers: MoMo disbursement or manual transfer, passenger SMS, `audit_log` entry, booking status update.
2. Every refund is traceable to an authorising person and a reason.
3. Refunds are executed within 24 hours of the task being created. Measured, not assumed.
4. A weekly query lists outstanding refund tasks.

---

## 8. Traceability

| Sprint                            | Use cases                                                     |
|-----------------------------------|---------------------------------------------------------------|
| **Sprint 0** (non-code, parallel) | UC-O-01 prerequisites; MoMo and SMS account applications      |
| **Sprint 1** — Inventory          | UC-P-01, UC-P-02, UC-P-03, UC-P-04, UC-A-03, UC-S-01, UC-S-06 |
| **Sprint 2** — Payment            | UC-P-05, UC-P-06, UC-P-07, UC-S-02, UC-S-03, UC-S-04          |
| **Sprint 3** — Ticket delivery    | UC-P-08, UC-P-09, UC-P-10, UC-S-05                            |
| **Sprint 4** — Counter            | UC-C-01 … UC-C-07                                             |
| **Sprint 5** — Controller         | UC-T-01 … UC-T-08                                             |
| **Sprint 6** — Launch             | UC-A-01, UC-A-02, UC-A-04, UC-P-11, UC-O-01, UC-O-02          |

---

## 9. Pre-launch verification

Before the first real passenger, these must all be true. Not "mostly true."

| #  | Check                                                                         | Status |
|----|-------------------------------------------------------------------------------|--------|
| 1  | Every P0 use case closed, all criteria passing                                | ☐     |
| 2  | The 20-parallel-request concurrency test (UC-P-03 AC6) green in CI            | ☐     |
| 3  | The 30-transaction payment test (Build Spec §4) passing                       | ☐     |
| 4  | 20 boardings validated offline and synced with zero loss (UC-T-06, UC-T-07)   | ☐     |
| 5  | A real SMS received on both an MTN and an Orange handset in Cameroon          | ☐     |
| 6  | A cashier who has never seen the system sells 3 tickets unaided (UC-C-03 AC4) | ☐     |
| 7  | Booking flow completed end-to-end on a 4-year-old Android on 3G               | ☐     |
| 8  | Nightly reconciliation has run successfully for 7 consecutive days            | ☐     |
| 9  | Consent capture live and storing policy version (UC-P-04 AC6)                 | ☐     |
| 10 | Refund procedure executed at least once, on a test booking                    | ☐     |
| 11 | WhatsApp support number live, staffed, and printed on every ticket            | ☐     |
| 12 | Rollback plan written: how the agency reverts to paper if JEMIL is down       | ☐     |

> Item 12 is not pessimism. The agency will ask you this question in the first meeting, and having a good answer is a large part of why they will say yes.
