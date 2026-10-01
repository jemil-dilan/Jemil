# JEMIL — Detailed Use Case Specifications
## Part 1 of 2 — Passenger app & system jobs (Sprints 1–3)

**Version:** 1.0 · **Date:** 11 August 2026
**Companion documents:** `JEMIL_MVP_Build_Spec.md` (architecture, data model, payment state machine) · `JEMIL_Use_Cases_Acceptance_Criteria.md` (acceptance criteria — **not repeated here**)

---

## How to read this document

Each use case gives you: the flows, the business rules, the data written, the endpoints touched, and a **task breakdown** you can turn directly into tickets. Acceptance criteria are referenced by ID and live in the companion document.

**Convention:**
- `BR-n` — business rule. These are the statements to check when a bug report arrives.
- `A-n` — alternate flow (a legitimate variation of the main path).
- `E-n` — exception flow (something went wrong).
- Estimates assume one developer of solid mid-level ability, and include writing the tests.

**Terminology note:** the UI is French-first. Where I give a string, the French is authoritative; the English is a translation, not the other way round.

---

# UC-P-01 · Rechercher un départ

|                |                                                                       |
|----------------|-----------------------------------------------------------------------|
| **Actor**      | Passenger (unauthenticated)                                           |
| **Priority**   | P0 · **Sprint** 1 · **Estimate** 3 days                               |
| **Depends on** | UC-A-03, UC-S-06 (trips must exist before search can return anything) |
| **Criteria**   | See UC-P-01, AC 1–8                                                   |

**Goal.** Find bookable departures between two cities on a chosen date.

**Trigger.** Passenger opens the site, or edits an existing search.

**Preconditions.** At least one `trip` with `status = 'scheduled'`; cities present in `route`.

**Postconditions.** *Success:* a list of departures is displayed and each is selectable or explicitly marked full. *Failure:* an empty state naming the next available date. No data is written in either case.

### Main success scenario

1. System renders the search form: **Départ** (city select), **Arrivée** (city select), **Date** (date picker, defaults to today).
2. Passenger selects origin. System filters the destination list to cities actually reachable from that origin.
3. Passenger selects destination and date.
4. Passenger taps **Rechercher**.
5. System queries departures matching the criteria.
6. System renders a result card per departure: agency name, departure time, estimated arrival, price, class badge, seats remaining.
7. Passenger taps a card → UC-P-02.

### Alternate flows

- **A-1 · Deep link.** Passenger arrives at `/recherche?from=DLA&to=YDE&date=2026-08-14`. Skip to step 5, with the form pre-filled and editable.
- **A-2 · Same-day search after cutoff.** Departures within 30 minutes are excluded from results. If this empties the list, show *« Plus de départs aujourd'hui pour cet itinéraire »* with a one-tap link to tomorrow.
- **A-3 · Reversed route.** Offer a swap control (⇅) between the two city fields. Cameroonians travel round trips; this saves four taps.

### Exception flows

- **E-1 · No departures on the requested date.** Empty state showing the next date that has availability, with a one-tap jump. Never a blank page.
- **E-2 · Past date submitted.** Server returns 400. Client should have prevented it; if the server sees one, log it — it means client validation is broken.
- **E-3 · Network failure.** *« Connexion perdue. Réessayez. »* with a retry button. The form retains all inputs.

### Business rules

- **BR-1** Only `status = 'scheduled'` trips are searchable. `boarding`, `departed`, `arrived` and `cancelled` are excluded.
- **BR-2** Departure cutoff is 30 minutes before `departure_at`. Configurable per agency later; hard-coded for MVP.
- **BR-3** `seats_available = trip.seats_total − count(seat_assignment WHERE status IN ('held','sold'))`. Held seats count as unavailable — a passenger mid-payment owns that seat.
- **BR-4** Full trips are displayed, not hidden. Seeing "Complet" tells the passenger the route is real and busy; hiding it looks like the route doesn't exist.
- **BR-5** Sort by departure time ascending. Not by price, not by agency — travellers choose by when they want to leave.
- **BR-6** Money renders as `5 000 FCFA` — space as thousands separator, `FCFA` not `XAF`. XAF is the ISO code; nobody in Douala says it.

### Data

Reads `trip`, `agency`, `route`, `bus`, `seat_assignment`. Writes nothing.

### Endpoints

`GET /api/v1/trips?from={city}&to={city}&date={ISO date}`

### UI notes

Mobile-first. The result card is a single tap target — no nested buttons. Class badge: **VIP** in solid orange, **Standard** as a neutral outline. Show seats remaining only when below 10, phrased as *« 4 places restantes »*; above that it reads as desperate marketing.

### Tasks

- [ ] `route` and `trip` query with composite index on `(origin_city, dest_city, departure_at, status)`
- [ ] `GET /trips` endpoint with parameter validation
- [ ] Seats-available computation (view or subquery — benchmark both)
- [ ] Search form component with dependent city selects
- [ ] Result card component
- [ ] Empty state with next-available-date lookup
- [ ] FR/EN strings for this screen
- [ ] Currency and date formatting helpers (`fr-CM` locale)
- [ ] 3G performance check against AC-6

---

# UC-P-02 · Consulter le plan de sièges

|                |                                         |
|----------------|-----------------------------------------|
| **Actor**      | Passenger                               |
| **Priority**   | P0 · **Sprint** 1 · **Estimate** 3 days |
| **Depends on** | UC-P-01                                 |
| **Criteria**   | See UC-P-02, AC 1–6                     |

**Goal.** See which seats are free on a chosen departure.

**Trigger.** Passenger selects a departure from search results.

**Preconditions.** Trip exists and is `scheduled`; its `bus` has a valid layout.

**Postconditions.** Seat map displayed with accurate availability. Nothing written.

### Main success scenario

1. System loads trip detail and seat availability.
2. System renders the bus layout: driver position, aisle, rows, seat numbers.
3. Free seats are interactive; taken seats are visually distinct and inert.
4. A summary bar shows agency, route, departure time, price per seat.
5. Every 20 seconds, the system silently refetches availability and updates the map.
6. Passenger taps a free seat → UC-P-03.

### Alternate flows

- **A-1 · Trip fills while viewing.** The refresh reveals zero free seats. Display *« Ce départ est complet »* and offer the next departure on the same route.
- **A-2 · Passenger's selected seat is taken by someone else during viewing.** Deselect it, flash the seat red once, show *« Le siège 12 vient d'être pris »*. Do not silently move their selection to another seat.

### Exception flows

- **E-1 · Refresh call fails.** Keep displaying the last known state and show a small *« Mise à jour… »* indicator. Do not blank the map — a stale map is far better than no map.
- **E-2 · Trip cancelled while viewing.** Full-screen message, return to search.

### Business rules

- **BR-1** Layout comes from `bus.seat_layout` and `bus.seat_count`. Never hard-code 40 seats; agencies run 30-, 50- and 70-seat vehicles.
- **BR-2** Availability is never more than 20 seconds stale.
- **BR-3** The driver position is rendered and labelled but is not a seat and carries no number.
- **BR-4** Taken seats differ from free seats in **fill, border and cursor** — not colour alone. Phone screens in direct Cameroonian sunlight wash out colour almost completely.
- **BR-5** Seat numbering follows the agency's physical numbering, which starts at 1 behind the driver. Confirm this per agency during onboarding; getting it wrong causes arguments at boarding.

### Data

Reads `trip`, `bus`, `seat_assignment`. Writes nothing.

### Endpoints

`GET /api/v1/trips/:id` · `GET /api/v1/trips/:id/seats`

### UI notes

Seat states: free (white, dark border), selected (orange fill, white number), taken (grey fill, muted number, no border). Minimum tap target 44 × 44 px. Whole map fits 360 px width without horizontal scroll — for a 70-seat bus this means vertical scrolling with a sticky summary bar.

### Tasks

- [ ] Seat layout parser (`'2+2'` → render grid)
- [ ] `GET /trips/:id/seats` returning `taken[]`
- [ ] Seat map component with three visual states
- [ ] 20-second polling hook with selection preservation
- [ ] Sticky trip summary bar
- [ ] Handle bus sizes 30 / 50 / 70 in tests
- [ ] Sunlight legibility check on a physical phone outdoors

---

# UC-P-03 · Sélectionner un siège et poser une réservation temporaire

|                |                                                            |
|----------------|------------------------------------------------------------|
| **Actor**      | Passenger                                                  |
| **Priority**   | P0 · **Sprint** 1 · **Estimate** 5 days                    |
| **Depends on** | UC-P-02                                                    |
| **Criteria**   | See UC-P-03, AC 1–9 — **AC-6 is the gate for this sprint** |

> The single most important use case in the build. Everything else can be repaired after launch. A double-sold seat cannot.

**Goal.** Lock a specific seat for long enough to complete payment.

**Trigger.** Passenger taps **Continuer** with at least one seat selected.

**Preconditions.** Trip `scheduled`; selected seats currently free.

**Postconditions.** *Success:* one `booking` (`held`) and one `seat_assignment` (`held`) per seat, plus a running countdown. *Failure:* nothing written, fresh seat map returned.

### Main success scenario

1. Passenger taps one or more free seats. Each tap toggles selection.
2. A panel shows selected seat numbers and the running total.
3. Passenger taps **Continuer**.
4. System opens a transaction, inserts the `booking` with `status='held'` and `hold_expires_at = now() + 10 min`, inserts a `seat_assignment` per seat, commits.
5. System returns booking id, reference, amount, expiry.
6. Client displays a countdown and advances to UC-P-04.

### Alternate flows

- **A-1 · Multiple seats.** Up to 5 seats in one online booking. All succeed or all fail — never partial.
- **A-2 · Deselect all.** **Continuer** returns to disabled.
- **A-3 · Refresh during hold.** The client persists the booking id in `sessionStorage`; on reload it fetches the booking and resumes the countdown at the correct remaining time.

### Exception flows

- **E-1 · Seat taken between render and submit (the race).** Unique partial index raises a violation → transaction rolls back → 409 with the current `taken[]`. Client refreshes the map and shows *« Ce siège vient d'être pris. Choisissez-en un autre. »*
- **E-2 · Hold expires before payment.** UC-S-01 releases it. Client countdown hits zero, returns to the seat map with *« Votre réservation a expiré. Le siège est de nouveau disponible. »*
- **E-3 · More than 5 seats.** 400 with *« Maximum 5 places en ligne. Pour un groupe, contactez l'agence. »*
- **E-4 · Trip departed or cancelled between steps.** 409, return to search.

### Business rules

- **BR-1** Hold TTL is 10 minutes, in one named constant, not scattered as `600` across the codebase.
- **BR-2** Correctness rests on the **database constraint**, not application checks:
  ```sql
  CREATE UNIQUE INDEX seat_once_per_trip
    ON seat_assignment (trip_id, seat_no)
    WHERE status IN ('held','sold');
  ```
  Application-level "check then insert" loses races. Catch the constraint violation and translate it to 409.
- **BR-3** Booking and all seat assignments are inserted in one transaction. Partial state is impossible.
- **BR-4** The reference (`JML-8F3K2`) is generated at hold time, so support can be given about a booking that never completed.
- **BR-5** Max 5 seats online; the counter (UC-C-04) allows 10.
- **BR-6** A held seat is unavailable to everyone, including counter staff. The cashier sees it as taken with *« En cours de paiement »*.

### Data

Writes `booking` (status, hold_expires_at, ref, trip_id, amount_xaf), `seat_assignment` (one row per seat), `audit_log`.

### Endpoints

`POST /api/v1/bookings` → 201 `{ booking_id, ref, amount_xaf, hold_expires_at }` · 409 `{ error: 'seat_taken', taken: [...] }`

### UI notes

The countdown is prominent but not alarming — a calm bar reading *« Réservation valable encore 9:43 »*. Under 2 minutes it turns orange. Never red; red at a payment step makes people abandon.

### Tasks

- [ ] Migration: `seat_assignment` table + unique partial index
- [ ] Booking reference generator (Crockford-style alphabet, no `0 O 1 I`)
- [ ] `POST /bookings` with transactional insert
- [ ] Constraint-violation → 409 translation
- [ ] **Concurrency test: 20 parallel requests, assert 1 success / 19 conflicts / 1 DB row**
- [ ] Countdown component with `sessionStorage` resume
- [ ] Multi-seat selection with 5-seat cap
- [ ] 409 handling with seat map refresh
- [ ] FR/EN strings

---

# UC-P-04 · Saisir les informations du voyageur

|                |                                         |
|----------------|-----------------------------------------|
| **Actor**      | Passenger                               |
| **Priority**   | P0 · **Sprint** 1 · **Estimate** 2 days |
| **Depends on** | UC-P-03                                 |
| **Criteria**   | See UC-P-04, AC 1–6                     |

**Goal.** Capture who is travelling and how to reach them.

**Preconditions.** An active hold exists.

**Postconditions.** Passenger name, phone, optional email and consent record are attached to the booking.

### Main success scenario

1. System shows the form with the countdown still visible.
2. Passenger enters full name.
3. Passenger enters phone, field prefilled with `+237`.
4. Passenger optionally enters email.
5. Passenger ticks the consent checkbox.
6. Passenger taps **Continuer vers le paiement** → UC-P-05.

### Alternate flows

- **A-1 · Booking for someone else.** Name and phone are the *traveller's*, not the buyer's. Label it explicitly: *« Nom du voyageur »*. The payer's number is captured separately at UC-P-05.
- **A-2 · No email.** Permitted and normal. Do not nag.

### Exception flows

- **E-1 · Invalid phone.** Inline error *« Numéro camerounais invalide »*, submit blocked.
- **E-2 · Consent unticked.** Submit blocked with an inline message. Never auto-tick.
- **E-3 · Hold expires while typing.** Countdown hits zero → return to seat map, form data preserved in memory in case they rebook the same seat.

### Business rules

- **BR-1** Required: name, phone. Optional: email. Nothing else — every extra field costs conversions.
- **BR-2** Phone accepted in the formats people actually type: `+237650000000`, `237650000000`, `650000000`, `6 50 00 00 00`. Normalise to E.164 on the server.
- **BR-3** Names must accept `À-ÿ`, apostrophes, hyphens, spaces. A validator that rejects `N'Guessan` or `Mbarga-Fouda` is a broken validator.
- **BR-4** Consent stored with timestamp, IP and policy version — a boolean alone will not satisfy loi 2024/017.
- **BR-5** Form state survives a failed payment. Retyping details after a failure is where people give up.

### Data

Updates `booking` (passenger_name, passenger_msisdn, passenger_email); inserts `consent_record`.

### Endpoints

`PATCH /api/v1/bookings/:id/passenger`

### Tasks

- [ ] Cameroonian MSISDN normaliser + validator (unit-tested against all four input formats)
- [ ] Name validator accepting accents, apostrophes, hyphens
- [ ] `consent_record` table + policy versioning
- [ ] Passenger form with inline validation
- [ ] Form state persistence across payment failure
- [ ] `PATCH` endpoint

---

# UC-P-05 · Payer par MTN Mobile Money

|                |                                         |
|----------------|-----------------------------------------|
| **Actor**      | Passenger + MTN MoMo                    |
| **Priority**   | P0 · **Sprint** 2 · **Estimate** 8 days |
| **Depends on** | UC-P-04; MoMo sandbox credentials       |
| **Criteria**   | See UC-P-05, AC 1–10                    |

> Budget the most time here. The mockup renders this as one button; it is a distributed transaction against a system you do not control.

**Goal.** Collect payment and convert the hold into a confirmed ticket.

**Preconditions.** Hold active, passenger details captured.

**Postconditions.** *Success:* `booking = paid`, seats `sold`, ticket SMS queued. *Failure:* seats released, booking recoverable.

### Main success scenario

1. System shows the order summary: route, date, departure, agency, seat(s), total.
2. Passenger confirms or edits the paying phone number (defaults to the traveller's).
3. Passenger taps **Payer 5 000 FCFA**.
4. System extends `hold_expires_at` to cover the payment window, generates an idempotency key, and writes a `payment` row with `status='initiated'` — **before** calling MoMo.
5. System calls MoMo `RequestToPay`.
6. MoMo returns 202. System sets `status='pending'`.
7. UI enters the pending state: *« Consultez votre téléphone et saisissez votre code MoMo. »* with a spinner and elapsed time.
8. Client polls `GET /payments/:id` every 3 seconds.
9. Passenger enters their PIN on their handset.
10. MoMo posts a success callback (UC-S-02).
11. System sets `booking='paid'`, all seats `'sold'`, queues the ticket SMS.
12. Next poll returns success; UI advances to UC-P-08.

### Alternate flows

- **A-1 · Different payer.** A relative pays. Payer MSISDN differs from traveller MSISDN; both are stored. The ticket SMS goes to the **traveller**; a short confirmation goes to the **payer**.
- **A-2 · Callback arrives before polling detects it.** Callback is authoritative; the poll simply reports the already-final state.
- **A-3 · Passenger backgrounds the browser.** On return, the client resumes polling from the stored payment id.

### Exception flows

- **E-1 · MoMo rejects the request (400/403).** → UC-P-06.
- **E-2 · MoMo unreachable.** Retry twice with backoff. Still failing → `status='failed'`, release seat, show *« Service de paiement momentanément indisponible »* plus the WhatsApp number.
- **E-3 · No resolution within 120 s.** → UC-P-07.
- **E-4 · Double submit.** Button disabled client-side; server rejects the duplicate idempotency key with the existing payment's state.
- **E-5 · Amount mismatch in request body.** Ignore the client value, use the server-derived amount, log at WARN. Repeated occurrences mean someone is probing you.

### Business rules

- **BR-1** `amount_xaf = trip.price_xaf × seat count`, computed server-side. The client never supplies an amount.
- **BR-2** The `payment` row is written **before** the outbound call. If you write it after and the process dies mid-call, you have taken money you have no record of.
- **BR-3** One idempotency key per attempt. A retry is a new key. Reusing a key is the mistake that causes double charges.
- **BR-4** Initiating payment extends the hold — an in-flight payment must never be killed by hold expiry (**AC-5**).
- **BR-5** Poll interval 3 s, timeout 120 s, both named constants.
- **BR-6** The confirmation screen appears only after server-side confirmation. Never optimistically.
- **BR-7** Payer MSISDN must be MTN for MoMo. Validate the prefix and say so plainly if it isn't: *« Ce numéro n'est pas un numéro MTN. »*

### Data

Writes `payment` (idempotency_key, provider_ref, amount, status, payer_msisdn, raw_response), updates `booking`, `seat_assignment`, `trip.seats_sold`, `audit_log`.

### Endpoints

`POST /api/v1/bookings/:id/pay` → 202 `{ payment_id, poll_after_ms }` · `GET /api/v1/payments/:id` → `{ status, booking_status, message_fr, message_en }`

### UI notes

The pending state is the screen people stare at while anxious. Show elapsed seconds, a plain-language instruction, and — after 45 seconds — *« Cela prend parfois jusqu'à deux minutes. »* Never a bare spinner.

### Tasks

- [ ] MoMo sandbox account + API user/key provisioning
- [ ] MoMo client wrapper (auth token caching, `RequestToPay`, status query)
- [ ] `payment` table migration
- [ ] Idempotency key generation and uniqueness enforcement
- [ ] `POST /bookings/:id/pay` with pre-write ordering
- [ ] Hold extension on payment initiation
- [ ] `GET /payments/:id` status endpoint with localised messages
- [ ] Client polling hook with 120 s timeout and background-resume
- [ ] Pending-state UI with elapsed timer
- [ ] Server-side amount derivation + tamper logging
- [ ] MTN prefix validation
- [ ] 30-transaction sandbox test suite (see Build Spec §4)

---

# UC-P-06 · Échec du paiement

|              |                                         |
|--------------|-----------------------------------------|
| **Actor**    | Passenger + MoMo                        |
| **Priority** | P0 · **Sprint** 2 · **Estimate** 2 days |
| **Criteria** | See UC-P-06, AC 1–6                     |

**Goal.** Fail clearly enough that the passenger knows what to do next.

**Trigger.** MoMo returns a definitive failure.

**Postconditions.** Seats released, booking recoverable, passenger informed in plain French.

### Main success scenario

1. Callback or poll returns a failure code.
2. System maps the code to a human message.
3. System sets `payment='failed'`, releases seat assignments, sets `booking='held'` if TTL remains, otherwise `'expired'`.
4. UI shows the reason plus **Réessayer**.
5. Retry → back to UC-P-05 step 3 with a new idempotency key and details intact.

### Business rules

- **BR-1** Failure code → message map (extend as the sandbox reveals more):

| Provider reason                   | French shown to passenger                           |
|-----------------------------------|-----------------------------------------------------|
| `NOT_ENOUGH_FUNDS`                | Solde insuffisant sur votre compte MoMo.            |
| `PAYER_LIMIT_REACHED`             | Vous avez atteint votre limite de transaction MoMo. |
| `PAYEE_NOT_FOUND` / config errors | Erreur technique. Contactez-nous sur WhatsApp.      |
| `PAYER_NOT_FOUND`                 | Ce numéro n'a pas de compte MoMo actif.             |
| `EXPIRED` / no PIN entered        | Vous n'avez pas confirmé le paiement à temps.       |
| `REJECTED`                        | Paiement annulé.                                    |
| anything unmapped                 | Le paiement n'a pas abouti. Réessayez.              |

- **BR-2** Never show the raw provider code. It is meaningless to the passenger and looks like a broken product.
- **BR-3** Every retry gets a fresh idempotency key (**BR-3 of UC-P-05**).
- **BR-4** After 3 consecutive failures, surface the WhatsApp support number prominently.
- **BR-5** Configuration failures (`PAYEE_NOT_FOUND`) are **your** bug, not the passenger's. Alert the operator immediately — this means your merchant account is misconfigured and nobody can pay.

### Tasks

- [ ] Failure code mapping table + FR/EN strings
- [ ] Seat release on failure with TTL check
- [ ] Retry flow preserving passenger details
- [ ] Consecutive-failure counter → support prompt
- [ ] Operator alert on configuration-class failures
- [ ] Test each sandbox failure code end-to-end

---

# UC-P-07 · Délai de paiement dépassé

|              |                                         |
|--------------|-----------------------------------------|
| **Actor**    | Passenger + MoMo                        |
| **Priority** | P0 · **Sprint** 2 · **Estimate** 2 days |
| **Criteria** | See UC-P-07, AC 1–5                     |

> The subtlety: a timeout is **not** a failure. The payment may still succeed. Telling the passenger "payment failed" and then taking their money is how you lose a market's trust permanently.

**Goal.** Stop waiting, without claiming an outcome you don't know.

**Trigger.** 120 seconds elapse with the payment still `pending`.

**Postconditions.** `payment='timeout'`, seats released, passenger correctly informed that the outcome is unknown, late resolution delegated to UC-S-03.

### Main success scenario

1. Timer reaches 120 s with no resolution.
2. System sets `payment='timeout'`, releases seats, sets `booking='expired'`.
3. UI shows:
   > *« Le paiement n'a pas abouti dans le délai imparti. Si votre compte a été débité, vous recevrez votre billet par SMS dans quelques minutes. Sinon, aucun montant ne sera prélevé. »*
4. WhatsApp support number displayed.
5. Options offered: **Réessayer** or **Retour**.

### Business rules

- **BR-1** The word *échec* (failure) must not appear. The correct framing is "did not complete in time".
- **BR-2** Seats are released — you cannot hold inventory on an unknown outcome.
- **BR-3** The payment record stays open, not closed. UC-S-03 may still resolve it.
- **BR-4** If the passenger retries and *both* payments later succeed, one must be refunded. UC-S-03 handles this; make sure it is tested.
- **BR-5** Timeout counts are monitored. A rising rate means MoMo degradation and should alert you before passengers tell you.

### Tasks

- [ ] Server-side timeout job (do not rely on the client timing out)
- [ ] Timeout copy, FR/EN, reviewed by a native French speaker
- [ ] Seat release on timeout
- [ ] Timeout rate metric + alert threshold
- [ ] Test: double-payment scenario where both eventually succeed

---

# UC-P-08 · Recevoir le billet

|                |                                         |
|----------------|-----------------------------------------|
| **Actor**      | Passenger + SMS provider                |
| **Priority**   | P0 · **Sprint** 3 · **Estimate** 3 days |
| **Depends on** | UC-P-05, UC-S-05                        |
| **Criteria**   | See UC-P-08, AC 1–6                     |

**Goal.** Put a usable ticket in the passenger's hands, including on a phone with no data and no charge.

**Trigger.** Payment confirmed.

**Postconditions.** SMS dispatched; confirmation screen shown; boarding code hashed in the database.

### Main success scenario

1. System generates a 6-digit boarding code and a signed QR token.
2. System stores `bcrypt(code)` and the QR token; the plaintext code exists only in the outbound SMS.
3. System queues the ticket SMS (UC-S-05).
4. Confirmation screen shows reference, QR, seat, route, departure, agency, amount, and the boarding code.
5. SMS arrives on the traveller's phone.

### Alternate flows

- **A-1 · Payer ≠ traveller.** Traveller gets the full ticket SMS; payer gets a short confirmation with the reference only — **not** the boarding code.
- **A-2 · Multiple seats.** One reference, one SMS listing all seats, **one boarding code per seat**. The controller validates per seat.

### Exception flows

- **E-1 · SMS fails all retries.** Confirmation screen still shows the code. Operator is alerted. Support can resend (UC-C-06).
- **E-2 · Passenger closes the browser before the screen renders.** SMS is the fallback of record — this is precisely why SMS is not optional.

### Business rules

- **BR-1** Boarding code: 6 digits, cryptographically random, unique within a trip.
- **BR-2** Stored as bcrypt only. Never logged, never in an API list response, never in an export.
- **BR-3** QR is a signed JWT: `{ booking_id, trip_id, seat_no, exp }`, `exp` = departure + 6 h.
- **BR-4** SMS aims for a single 160-char GSM-7 segment. Measure segments programmatically; assume nothing. Avoid characters that force UCS-2 (curly quotes, `€`, some accents) — one stray character doubles your SMS cost across every ticket.
- **BR-5** SMS language matches the language chosen at booking.

**SMS template (FR):**
```
JEMIL - Reservation confirmee
JML-8F3K2 | Siege 12
General Express DLA>YDE
Ven 14 aout, 06h00
Code embarquement: 483920
Aide: +237 6XX XXX XXX
```
Note the deliberate absence of accents — this keeps the message in GSM-7 and halves the cost.

### Data

Writes `boarding_pass` (code_hash, qr_token), `sms_log`.

### Tasks

- [ ] Boarding code generator with per-trip uniqueness
- [ ] bcrypt hashing at appropriate cost factor (benchmark: verification must stay under 1 s on a low-end device for UC-T-04)
- [ ] QR JWT signing + key management
- [ ] SMS template with segment-count test
- [ ] GSM-7 safe character enforcement
- [ ] Confirmation screen with QR rendering
- [ ] Payer-vs-traveller message split
- [ ] Real-device delivery test, MTN and Orange

---

# UC-P-09 · Retrouver un billet

|              |                                         |
|--------------|-----------------------------------------|
| **Actor**    | Passenger                               |
| **Priority** | P1 · **Sprint** 3 · **Estimate** 2 days |
| **Criteria** | See UC-P-09, AC 1–5                     |

**Goal.** Recover ticket details after losing the SMS.

### Main success scenario

1. Passenger opens **Suivre mon voyage**.
2. Enters reference and phone number.
3. System verifies both match the same booking.
4. Ticket details and current trip status are displayed.

### Business rules

- **BR-1** Reference **and** phone are both required. Reference alone would let anyone enumerate tickets.
- **BR-2** A wrong combination returns a generic *« Billet introuvable »* — never "reference exists but phone is wrong", which confirms a valid reference to an attacker.
- **BR-3** Rate limit 5/minute per IP.
- **BR-4** The boarding code is **not** shown in lookup by default; require an extra tap on **Afficher le code**, and log each reveal.
- **BR-5** Works for counter-sold tickets, which is how a passenger who bought at the guichet checks their departure.
- **BR-6** Trip status displayed as *Programmé* / *Embarquement* / *Parti* / *Arrivé* / *Annulé*, derived from `trip.status` — no GPS, no estimated position.

### Endpoints

`GET /api/v1/bookings/lookup?ref={ref}&msisdn={msisdn}`

### Tasks

- [ ] Lookup endpoint with paired verification
- [ ] Rate limiting middleware
- [ ] Generic not-found response
- [ ] Lookup form + ticket display
- [ ] Code reveal with audit logging
- [ ] Status label mapping FR/EN

---

# UC-P-10 · Abandon d'une réservation

|                |                                        |
|----------------|----------------------------------------|
| **Actor**      | Passenger + SYS                        |
| **Priority**   | P1 · **Sprint** 3 · **Estimate** 1 day |
| **Depends on** | UC-S-01                                |
| **Criteria**   | See UC-P-10, AC 1–3                    |

**Goal.** Ensure abandoned holds never permanently consume inventory.

### Main success scenario

1. Passenger closes the browser mid-hold.
2. The hold remains until `hold_expires_at`.
3. UC-S-01 releases it within 60 seconds of expiry.
4. The seat reappears on the public map.

### Business rules

- **BR-1** No explicit cancel action in the MVP. Passengers abandon by leaving; expiry handles it.
- **BR-2** No `held` booking older than 15 minutes may exist at any moment. A nightly query asserts this; a non-zero result is a bug in the expiry job.
- **BR-3** Expired bookings are retained, not deleted — abandonment rate by step is one of the few analytics worth having early.

### Tasks

- [ ] Nightly orphan-hold assertion query + alert
- [ ] Abandonment funnel logging (which step they left at)

---

# UC-P-11 · Changer de langue

|              |                                         |
|--------------|-----------------------------------------|
| **Actor**    | Passenger                               |
| **Priority** | P2 · **Sprint** 6 · **Estimate** 2 days |
| **Criteria** | See UC-P-11, AC 1–4                     |

**Goal.** Serve anglophone Cameroonians and diaspora users without making French users work for it.

### Business rules

- **BR-1** French is the default regardless of `Accept-Language`. The current prototype loads in English; that is backwards for this corridor.
- **BR-2** The toggle switches everything: interface, validation messages, error messages, SMS templates.
- **BR-3** Choice persists per session (cookie), and the language active at booking is stored on the booking and governs all SMS for it.
- **BR-4** No string is hard-coded in a component. Every user-facing string goes through the i18n layer from Sprint 1 — retrofitting i18n across a finished app costs several days.

### Tasks

- [ ] i18n framework wired in **Sprint 1**, not Sprint 6
- [ ] FR/EN string extraction and review by a native speaker of each
- [ ] Language toggle component
- [ ] Language stored on booking, used by SMS templates
- [ ] Lint rule failing the build on hard-coded user-facing strings

---

# UC-S-01 · Libérer les réservations expirées

|              |                                         |
|--------------|-----------------------------------------|
| **Actor**    | SYS                                     |
| **Priority** | P0 · **Sprint** 1 · **Estimate** 2 days |
| **Criteria** | See UC-S-01, AC 1–5                     |

**Goal.** Return abandoned inventory to sale.

**Trigger.** Scheduled, every 60 seconds.

### Main success scenario

1. Job selects bookings with `status='held'` and `hold_expires_at < now()`.
2. For each, in a transaction: delete or void seat assignments, set booking `'expired'`, write `audit_log`.
3. Job logs the count released.

### Business rules

- **BR-1** **Never release a hold whose booking has a payment in `initiated` or `pending`.** This is the rule that prevents cancelling a seat out from under a passenger who is entering their PIN. Get it wrong and you will produce the worst possible outcome: money taken, no seat.
- **BR-2** Idempotent — a second run in the same minute is a no-op.
- **BR-3** Uses row-level locking (`SELECT ... FOR UPDATE SKIP LOCKED`) so overlapping runs can't double-process.
- **BR-4** An unusually large release count triggers an alert — it usually means payments are broken upstream.

### Tasks

- [ ] Scheduled job infrastructure (`pg-boss` or `node-cron`)
- [ ] Expiry query with in-flight-payment exclusion
- [ ] `FOR UPDATE SKIP LOCKED` locking
- [ ] Release count metric + alert threshold
- [ ] Test: hold with pending payment is **not** released
- [ ] Test: concurrent job runs don't double-process

---

# UC-S-02 · Traiter un callback MoMo

|              |                                         |
|--------------|-----------------------------------------|
| **Actor**    | SYS + MoMo                              |
| **Priority** | P0 · **Sprint** 2 · **Estimate** 3 days |
| **Criteria** | See UC-S-02, AC 1–6                     |

**Goal.** Turn a provider notification into a correct state change, exactly once.

### Main success scenario

1. MoMo POSTs to `/webhooks/momo`.
2. System verifies source/signature.
3. System persists the raw payload immediately.
4. System looks up the payment by idempotency key.
5. If already in a final state → return 200, change nothing.
6. Otherwise apply the state transition (success → UC-P-05 step 11; failure → UC-P-06; success on a timed-out payment → UC-S-03).
7. Return 200.

### Business rules

- **BR-1** **Always return 200**, even on internal error — a non-200 makes the provider retry indefinitely and can amplify an outage into a flood. Log and alert internally instead.
- **BR-2** Idempotency is on the key, not the payload. Three deliveries → one state change. This is an explicit test, not an assumption.
- **BR-3** Raw payload is stored before processing. When you are debugging a disputed transaction in three months, this row is the only evidence you have.
- **BR-4** A callback for an unknown payment is logged **and alerted**, never silently dropped — it may mean you have taken money against a record you lost.
- **BR-5** Processing target under 500 ms; anything slower goes on a queue.
- **BR-6** The endpoint is public. Verify aggressively: source IP allowlist where the provider supports it, plus signature or shared-secret validation.

### Endpoints

`POST /api/v1/webhooks/momo`

### Tasks

- [ ] Webhook endpoint with always-200 semantics
- [ ] Signature/source verification
- [ ] Raw payload persistence before processing
- [ ] Idempotent state transition handler
- [ ] Unknown-payment alerting
- [ ] **Test: same callback delivered 3× → exactly 1 state change**
- [ ] Test: malformed payload → 200 + internal alert

---

# UC-S-03 · Traiter un paiement abouti tardivement

|              |                                         |
|--------------|-----------------------------------------|
| **Actor**    | SYS                                     |
| **Priority** | P0 · **Sprint** 2 · **Estimate** 3 days |
| **Criteria** | See UC-S-03, AC 1–5                     |

> The scenario: you timed out at 120 s; the payment succeeded at 180 s; you are holding a passenger's money. What happens next determines whether that passenger — and everyone they tell — ever uses JEMIL again.

**Goal.** Guarantee that JEMIL never keeps money without delivering a ticket.

**Trigger.** A success callback arrives for a payment in `timeout` state.

### Main success scenario — seats still free

1. Callback arrives; payment is `timeout`.
2. System checks whether all original seats are still free.
3. They are: reassign them as `sold`, set booking `'paid'`, set payment `'reconciled_late'`.
4. Generate boarding pass, queue the ticket SMS.
5. Passenger receives their ticket, several minutes late but correct.

### Alternate flow — seats no longer free

1. Steps 1–2 as above; at least one seat is now sold.
2. System sets payment `'refund_due'` and creates a refund task.
3. Within 5 minutes the passenger receives:
   > *« Votre paiement a ete recu apres l'expiration du delai et le siege n'etait plus disponible. Nous vous remboursons integralement sous 24h. Desole. Aide: +237 6XX XXX XXX »*
4. Operator executes the refund (UC-O-02) within 24 hours.
5. `audit_log` records the full chain.

### Business rules

- **BR-1** A late success is **accepted**, never rejected as stale. Rejecting it is how money gets kept silently.
- **BR-2** Two outcomes only: ticket delivered, or money returned. No third branch exists.
- **BR-3** The apology SMS goes out within 5 minutes — the passenger discovers the problem from you, not at the gare.
- **BR-4** Refund tasks appear on an operator dashboard or daily digest that is actually read.
- **BR-5** Late-success frequency is tracked. If it exceeds ~2% of payments, raise the timeout above 120 s rather than accepting a steady stream of refunds.
- **BR-6** If the passenger retried and both payments succeeded, the *second* is refunded and the first honoured.

### Tasks

- [ ] Late-callback branch in the webhook handler
- [ ] Seat-availability recheck at reassignment time (in a transaction)
- [ ] `refund_task` table + creation logic
- [ ] Apology SMS template (GSM-7 safe, no accents)
- [ ] Refund task digest for the operator
- [ ] Late-success rate metric
- [ ] **Test: success callback at T+300 s, both branches**
- [ ] Test: double payment, both succeed → correct one refunded

---

# UC-S-04 · Rapprochement quotidien des paiements

|              |                                         |
|--------------|-----------------------------------------|
| **Actor**    | SYS + MoMo                              |
| **Priority** | P0 · **Sprint** 2 · **Estimate** 3 days |
| **Criteria** | See UC-S-04, AC 1–5                     |

**Goal.** Detect every discrepancy between what MoMo recorded and what JEMIL recorded, within 24 hours.

**Trigger.** Nightly, after midnight Africa/Douala.

### Main success scenario

1. Job pulls the provider's transaction list for the day.
2. Diffs it against `payment` rows for the same window.
3. Classifies discrepancies into three buckets.
4. Writes a `reconciliation_report` row.
5. Sends the report to the operator; any non-empty bucket is flagged as requiring action.

### Business rules

- **BR-1** Three buckets, each with a different meaning:

| Bucket                | Meaning                                                   | Severity |
|-----------------------|-----------------------------------------------------------|----------|
| In MoMo, not in JEMIL | Money taken, no record. **Someone paid and got nothing.** | Critical |
| In JEMIL, not in MoMo | Local record of a payment that never happened.            | High     |
| Amount mismatch       | Wrong sum charged.                                        | Critical |

- **BR-2** A non-empty result **alerts**; it does not merely log. A log line nobody reads is not reconciliation.
- **BR-3** Reports retained 12 months minimum — tax and dispute evidence.
- **BR-4** Runs from day one of live payments. The first real discrepancy will not announce itself.
- **BR-5** A day with zero transactions still produces a report. Silence must be distinguishable from breakage.

### Tasks

- [ ] Provider transaction-list API integration
- [ ] Diff engine with three-bucket classification
- [ ] `reconciliation_report` table
- [ ] Operator notification (email or WhatsApp)
- [ ] Retention policy
- [ ] Test: seeded discrepancy of each type is detected

---

# UC-S-05 · Envoyer les SMS avec réessai

|              |                                         |
|--------------|-----------------------------------------|
| **Actor**    | SYS + SMS provider                      |
| **Priority** | P0 · **Sprint** 3 · **Estimate** 3 days |
| **Criteria** | See UC-S-05, AC 1–6                     |

**Goal.** Deliver SMS reliably without coupling delivery to a web request.

### Main success scenario

1. A use case enqueues a message with a template id, recipient, variables and priority.
2. Worker picks it up, renders it, sends it.
3. Provider reference and status are recorded in `sms_log`.
4. Delivery receipt, if provided, updates the row.

### Business rules

- **BR-1** Never send inline during an HTTP request. A slow provider must not slow a booking.
- **BR-2** Three retries with exponential backoff (10 s, 60 s, 300 s).
- **BR-3** After three failures: mark failed, alert the operator. Someone must know the passenger has no ticket.
- **BR-4** Priority ordering — ticket SMS ahead of everything else. Ticket delivery is the product.
- **BR-5** Every message logged with cost. At 10 FCFA a segment against ~100–200 FCFA net revenue per ticket, SMS is a material cost line and must be visible daily.
- **BR-6** Templates are versioned. When you change wording you must still be able to read what an old passenger actually received.
- **BR-7** Recipient numbers masked in application logs; full number only in `sms_log`.

### Tasks

- [ ] Queue + worker (`pg-boss`)
- [ ] SMS provider client
- [ ] `sms_log` table with cost tracking
- [ ] Template engine with versioning
- [ ] Retry with exponential backoff
- [ ] Priority queue for ticket messages
- [ ] Daily cost report
- [ ] Delivery receipt handling
- [ ] Test: provider down → 3 retries → alert

---

# UC-S-06 · Générer les voyages depuis les horaires récurrents

|                |                                         |
|----------------|-----------------------------------------|
| **Actor**      | SYS                                     |
| **Priority**   | P0 · **Sprint** 1 · **Estimate** 2 days |
| **Depends on** | UC-A-03                                 |
| **Criteria**   | See UC-S-06, AC 1–4                     |

**Goal.** Keep a rolling 14-day window of bookable departures without human effort.

**Trigger.** Nightly.

### Main success scenario

1. Job loads all active `schedule_template` rows.
2. For each, computes which of the next 14 days match its days-of-week.
3. Inserts a `trip` for each date that does not already have one.
4. Logs the count created.

### Business rules

- **BR-1** Uniqueness enforced by a DB constraint on `(template_id, service_date)`, not by application logic.
- **BR-2** Never modifies an existing trip. Generation only inserts.
- **BR-3** Window is 14 days. Longer means more trips to cancel when a schedule changes; shorter risks running out if the job fails unnoticed.
- **BR-4** **Zero trips created on a normal night is an alert condition** — it almost always means generation is broken, and you would otherwise find out when a passenger reports an empty search.
- **BR-5** Public holidays are ignored in MVP. Agencies adjust manually.
- **BR-6** Runs in Africa/Douala time. A job running in UTC will generate the wrong days near midnight.

### Tasks

- [ ] `schedule_template` table + unique constraint on generated trips
- [ ] Nightly generation job
- [ ] Days-of-week matching with correct timezone handling
- [ ] Created-count metric with zero-alert
- [ ] Backfill command for initial setup and recovery
- [ ] Test: rerun on the same night creates nothing

---

## Sprint summary — Part 1

| Sprint              | Use cases                          | Estimated developer-days |
|---------------------|------------------------------------|--------------------------|
| 1 — Inventory       | P-01, P-02, P-03, P-04, S-01, S-06 | 17                       |
| 2 — Payment         | P-05, P-06, P-07, S-02, S-03, S-04 | 21                       |
| 3 — Ticket delivery | P-08, P-09, P-10, S-05             | 9                        |
| 6 — (deferred)      | P-11                               | 2                        |

**Part 1 total: ~49 developer-days.** At one developer that is roughly 10 working weeks; at two working in parallel with a clean interface split (one on payment/system, one on passenger UI) roughly 6.

Add 25–30% for integration, review and the things nobody estimates. Continues in **Part 2** with the counter, controller, agency and operator use cases.
