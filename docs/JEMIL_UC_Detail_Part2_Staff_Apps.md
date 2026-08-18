# JEMIL — Detailed Use Case Specifications
## Part 2 of 2 — Counter, controller, agency & operator (Sprints 4–6)

**Version:** 1.0 · **Date:** 11 August 2026
**Companion documents:** `JEMIL_UC_Detail_Part1_Passenger_System.md` · `JEMIL_MVP_Build_Spec.md` · `JEMIL_Use_Cases_Acceptance_Criteria.md`

Same conventions as Part 1. Acceptance criteria are referenced, not repeated.

---

## Why these use cases decide whether the pilot works

Part 1 builds a product a passenger can buy from. Part 2 builds the product the **agency** actually adopts — and adoption, not technology, is where this category of project usually dies.

Two things to hold in mind while implementing this half:

**The counter app is used by someone who did not ask for it**, standing at a window with a queue of eight people behind the person they are serving, in heat, under a supervisor's eye. If it is one second slower than the paper book, they will use the paper book, and your inventory becomes fiction. Speed is not a nice-to-have here; it is the requirement.

**The controller app is used in dust, noise and motion**, on a device with a cracked screen and 20% battery, by someone whose informal income you may be perceived as threatening. It must never lose data, never freeze, and never require a network.

---

# UC-C-01 · Connexion du caissier

| | |
|---|---|
| **Actor** | Cashier |
| **Priority** | P0 · **Sprint** 4 · **Estimate** 3 days |
| **Criteria** | See UC-C-01, AC 1–5 |

**Goal.** Let counter staff into the system quickly, with data isolated to their own agency.

**Preconditions.** A `staff_user` exists with `role='cashier'` and a PIN set during onboarding.

### Main success scenario

1. Cashier opens the counter app on the agency tablet.
2. Enters phone number and 4-digit PIN.
3. System verifies and issues a JWT valid 12 hours.
4. Cashier lands on today's departures (UC-C-02).

### Alternate flows

- **A-1 · Shared tablet, shift change.** The outgoing cashier signs out; the incoming one signs in. Each sale must attribute to the right person — this is the whole point of `sold_by_staff_id`, and it is also what makes end-of-day cash reconciliation possible.
- **A-2 · Session expiry mid-shift.** Re-authentication is PIN-only; the phone number is remembered on that device.

### Exception flows

- **E-1 · Wrong PIN.** Generic *« Numéro ou code incorrect »* — do not reveal which was wrong.
- **E-2 · Five failures.** Lock 15 minutes; notify the manager. Show remaining lock time.
- **E-3 · Suspended account.** *« Compte désactivé. Contactez votre responsable. »*
- **E-4 · Offline at sign-in.** A previously valid session on this device continues to work for read-only viewing; new sales require connectivity.

### Business rules

- **BR-1** Phone + 4-digit PIN. No email, no password. Counter staff will not manage a password, and requiring one guarantees it gets written on a sticky note beside the tablet.
- **BR-2** Token lifetime 12 hours — one working day, no mid-shift interruption.
- **BR-3** **Tenant isolation is enforced server-side on every query**, derived from the token's `agency_id`, never from a request parameter. This gets an explicit automated test that attempts cross-agency access with a valid token and expects 403.
- **BR-4** PINs are bcrypt-hashed. Delivered out-of-band at onboarding, never by SMS in the same message as anything else.
- **BR-5** Lockouts are logged and visible to the manager.

### Data

Reads `staff_user`. Writes `audit_log` (sign-in, failure, lockout).

### Endpoints

`POST /api/v1/counter/auth` → `{ token, expires_at, staff:{ name, agency } }`

### Tasks

- [ ] `staff_user` table + PIN hashing
- [ ] PIN auth endpoint with JWT issuance
- [ ] Failed-attempt counter + 15-minute lockout
- [ ] Tenant isolation middleware (`agency_id` from token)
- [ ] **Cross-agency access test expecting 403**
- [ ] Sign-in screen, large numeric keypad
- [ ] Remembered phone number per device
- [ ] Manager notification on lockout

---

# UC-C-02 · Consulter les départs du jour

| | |
|---|---|
| **Actor** | Cashier |
| **Priority** | P0 · **Sprint** 4 · **Estimate** 2 days |
| **Criteria** | See UC-C-02, AC 1–4 |

**Goal.** Give the cashier an at-a-glance view of what they are selling today.

### Main success scenario

1. System lists today's trips for this agency, ordered by departure time.
2. Each row: route, departure time, seats sold / capacity, status.
3. Upcoming and departed trips are visually separated.
4. Auto-refresh every 30 seconds.
5. Cashier taps a trip → UC-C-03.

### Alternate flows

- **A-1 · Tomorrow's departures.** A date selector allows selling for future dates — passengers routinely buy a day ahead.
- **A-2 · No departures today.** Empty state directing them to check the schedule with their manager.

### Business rules

- **BR-1** Today defaults; forward dates selectable up to the 14-day generation window.
- **BR-2** Departed trips remain visible but are not sellable.
- **BR-3** Occupancy shown as `24/40` with a progress bar — cashiers use this to push passengers toward emptier departures.
- **BR-4** 30-second refresh must not interrupt an in-progress sale.

### Endpoints

`GET /api/v1/counter/trips?date={date}`

### UI notes

Designed for a tablet in landscape on a counter, viewed at arm's length. Large type. High contrast. Assume glare.

### Tasks

- [ ] `GET /counter/trips` scoped to the token's agency
- [ ] Trip list with occupancy bars
- [ ] Date selector within the generation window
- [ ] Non-disruptive auto-refresh
- [ ] Tablet-landscape layout

---

# UC-C-03 · Vendre un billet au guichet

| | |
|---|---|
| **Actor** | Cashier |
| **Priority** | P0 · **Sprint** 4 · **Estimate** 5 days |
| **Criteria** | See UC-C-03, AC 1–8 — **AC-4 (under 30 seconds) is the gate** |

> This use case is the reason the whole system holds together. It is also the one that will be abandoned first if it is slow.

**Goal.** Sell a cash ticket through JEMIL so that online and counter inventory are the same inventory.

### Main success scenario

1. Cashier selects a departure.
2. Same live seat map the public sees, including online holds.
3. Cashier taps a free seat.
4. Enters passenger name and phone.
5. Confirms cash received.
6. System creates `booking` (`channel='counter'`, `status='paid'`) and `payment` (`provider='cash'`), assigns the seat as `sold`.
7. Boarding code displays large on screen; SMS is sent if a number was given.
8. Cashier returns to the trip for the next customer.

### Alternate flows

- **A-1 · No phone number.** Permitted. Some walk-in passengers have no phone at all. Code is shown on screen and written on the paper stub. This is normal, not an edge case.
- **A-2 · Passenger names a preferred seat.** They point at the map; cashier taps it. This is a selling point — passengers currently have no say.
- **A-3 · Seat taken by an online buyer mid-sale.** Refresh shows it grey with *« En cours de paiement »*; cashier picks another seat.

### Exception flows

- **E-1 · Connection lost mid-sale.** The sale either completes or fails cleanly. Never leave a seat ambiguous. On failure: *« Vente non enregistrée. Réessayez. »* — the cashier has not yet taken the money at this point in the flow, which is why step 5 comes before step 6.
- **E-2 · Seat lost to a race.** Same 409 handling as UC-P-03, worded for staff.
- **E-3 · SMS fails.** The sale still stands. The code is on screen. Show a small *« SMS non envoyé »* marker so the cashier tells the passenger to write the code down.

### Business rules

- **BR-1** Counter sales use the **same** `seat_assignment` table and the **same** unique constraint. There is no parallel counter inventory. This is the entire point.
- **BR-2** `sold_by_staff_id` is always populated. Without it there is no cash accountability, and cash accountability is what the agency owner is buying.
- **BR-3** Cash payments are recorded as `payment` rows with `provider='cash'` so that every ticket has a payment record and reconciliation covers all channels.
- **BR-4** Confirmation precedes commitment: money is taken at step 5, the record is written at step 6. If the order is reversed, a network failure leaves a sold seat with no cash in the drawer.
- **BR-5** **Target: under 30 seconds.** Measure with a stopwatch on a real cashier after ten repetitions. If it exceeds 30 s, remove fields until it doesn't.
- **BR-6** The seat must vanish from the public map within 20 seconds.
- **BR-7** Boarding code displayed at minimum 48 px — it must be readable across a counter, upside down if necessary.

### Data

Writes `booking`, `payment`, `seat_assignment`, `boarding_pass`, `sms_log`, `audit_log`.

### Endpoints

`POST /api/v1/counter/bookings`

### UI notes

Optimise ruthlessly for repetition. Name field autofocused. Phone keyboard numeric. One primary button. No confirmation dialog — a dialog costs two seconds per sale, which is four minutes across a busy morning. The undo path is UC-C-05.

### Tasks

- [ ] `POST /counter/bookings` (cash, immediate paid state)
- [ ] Shared seat map component reused from the passenger app
- [ ] Online-hold indication for staff
- [ ] Optional phone number path
- [ ] Large boarding code display
- [ ] `sold_by_staff_id` attribution
- [ ] Clean failure handling on connection loss
- [ ] **Stopwatch test with a real cashier, 10 repetitions**
- [ ] SMS-failure indicator

---

# UC-C-04 · Vendre plusieurs places en une transaction

| | |
|---|---|
| **Actor** | Cashier |
| **Priority** | P1 · **Sprint** 4 · **Estimate** 2 days |
| **Criteria** | See UC-C-04, AC 1–4 |

**Goal.** Serve families and groups without ten separate sales.

### Main success scenario

1. Cashier selects multiple seats (up to 10).
2. Enters the lead passenger's name and phone.
3. Optionally names each additional traveller.
4. Confirms the total.
5. One booking, one reference, one boarding code per seat.

### Business rules

- **BR-1** Counter cap is 10 seats (online is 5). Beyond 10, the manager arranges it directly.
- **BR-2** All-or-nothing. If any seat is lost during the transaction, the whole sale fails with a clear message. A partially completed group booking is worse than none — it splits a family across two buses.
- **BR-3** Per-seat names are optional; the lead passenger's name is used as a fallback on the manifest, suffixed `(2/4)`.
- **BR-4** One SMS listing all seats and all codes, provided it stays within 3 segments; otherwise one SMS per seat.

### Tasks

- [ ] Multi-seat selection in the counter map, cap 10
- [ ] Optional per-passenger names
- [ ] Transactional all-or-nothing insert
- [ ] Group SMS template with segment-count fallback
- [ ] Manifest rendering of group bookings

---

# UC-C-05 · Annuler une réservation

| | |
|---|---|
| **Actor** | Cashier + Manager |
| **Priority** | P1 · **Sprint** 4 · **Estimate** 3 days |
| **Criteria** | See UC-C-05, AC 1–6 |

**Goal.** Correct mistakes and handle no-shows without letting a cashier quietly void sales.

### Main success scenario

1. Cashier finds the booking (UC-C-06 search).
2. Taps **Annuler**.
3. System requests a **manager** PIN.
4. Manager enters PIN and selects a reason.
5. Booking → `cancelled`, seat released, SMS sent, `audit_log` written.
6. For MoMo-paid bookings, a refund task is created.

### Business rules

- **BR-1** Manager PIN required, never cashier PIN. Cancellation is the obvious fraud vector: sell a ticket, pocket the cash, void the record. This control is why the agency owner trusts the numbers.
- **BR-2** Reason from a fixed list — *Erreur de saisie*, *Demande du voyageur*, *Voyage annulé*, *Autre*. Free text only for *Autre*.
- **BR-3** Cash cancellations do not create a refund task; the cashier returns the cash. It is still logged.
- **BR-4** MoMo cancellations always create a refund task. JEMIL never keeps money for a cancelled ticket.
- **BR-5** Cancellation is blocked after departure. Post-departure disputes go through the manager and WhatsApp, outside the system.
- **BR-6** `audit_log` records both the cashier who initiated and the manager who authorised.

### Endpoints

`POST /api/v1/counter/bookings/:id/cancel` (manager PIN in body)

### Tasks

- [ ] Manager PIN verification (separate from session auth)
- [ ] Cancellation reasons enumeration
- [ ] Seat release + status transition
- [ ] Refund task creation for MoMo bookings
- [ ] Cancellation SMS template
- [ ] Dual-actor audit logging
- [ ] Post-departure block

---

# UC-C-06 · Rechercher et renvoyer un code d'embarquement

| | |
|---|---|
| **Actor** | Cashier |
| **Priority** | P1 · **Sprint** 4 · **Estimate** 2 days |
| **Criteria** | See UC-C-06, AC 1–4 |

**Goal.** Rescue the passenger who lost their SMS — which will be common.

### Main success scenario

1. Cashier searches by reference, phone, or name.
2. Selects the booking from the results.
3. Taps **Afficher le code** or **Renvoyer par SMS**.
4. The same code is displayed or resent.

### Business rules

- **BR-1** Resend issues the **same** code. Generating a new one would invalidate a code the passenger may already have written down.
- **BR-2** Codes never appear in list views — only after an explicit reveal on a single booking.
- **BR-3** Resend rate-limited to 3 per booking per hour; every reveal and resend is logged with the staff id.
- **BR-4** Name search is fuzzy — cashiers will type `Nfonka` for `Nfonkam`, and accents will be inconsistent.
- **BR-5** Search is scoped to this agency and to the last 7 days plus all future trips.

### Tasks

- [ ] Booking search (reference / phone / fuzzy name)
- [ ] Reveal action with audit logging
- [ ] Resend with same-code guarantee
- [ ] Per-booking rate limiting
- [ ] Accent-insensitive name matching

---

# UC-C-07 · Totaux de fin de journée

| | |
|---|---|
| **Actor** | Cashier |
| **Priority** | P1 · **Sprint** 4 · **Estimate** 2 days |
| **Criteria** | See UC-C-07, AC 1–4 |

**Goal.** Let the cashier balance their drawer, and give the manager a figure to check it against.

### Main success scenario

1. Cashier opens **Ma journée**.
2. Sees: tickets sold, cash total, MoMo total, combined — for today, for this cashier.
3. Prints or shows it to the manager at handover.

### Business rules

- **BR-1** Scoped to this cashier, not the agency. The cashier balances their own drawer.
- **BR-2** Figures must equal the sum of underlying `payment` rows exactly. An automated reconciliation test asserts this — a dashboard that disagrees with the ledger destroys trust in every other number you show.
- **BR-3** Cash and MoMo totals shown separately. Only cash should be in the drawer.
- **BR-4** Frozen at midnight Africa/Douala; past days remain retrievable.
- **BR-5** Cancelled sales are excluded from totals but shown as a separate count — that count is exactly what a manager wants to see.

### Endpoints

`GET /api/v1/counter/me/today`

### Tasks

- [ ] Per-cashier daily aggregation query
- [ ] Cash / MoMo split
- [ ] Cancellation count line
- [ ] Print-friendly layout
- [ ] **Reconciliation test: displayed totals == sum of payment rows**

---

# UC-T-01 · Connexion du contrôleur et sélection du voyage

| | |
|---|---|
| **Actor** | Controller |
| **Priority** | P0 · **Sprint** 5 · **Estimate** 2 days |
| **Criteria** | See UC-T-01, AC 1–3 |

**Goal.** Get the controller to the right manifest in as few taps as possible, while they are already busy.

### Main success scenario

1. Controller opens the scanner app.
2. Signs in with phone + PIN.
3. Sees this agency's trips departing within 4 hours.
4. Selects the trip → manifest download begins (UC-T-02).

### Business rules

- **BR-1** Same auth mechanism as the cashier — one implementation, two roles.
- **BR-2** Only trips within a 4-hour window. A controller boarding the 06:00 does not need to see the 18:00, and a short list is faster under pressure.
- **BR-3** Session lasts 12 hours so a controller never has to sign in at the bus door.
- **BR-4** The last selected trip is remembered; reopening the app resumes it directly.
- **BR-5** Sign-in requires connectivity; **everything after it must not**.

### Tasks

- [ ] Reuse PIN auth with `role='controller'`
- [ ] 4-hour trip window query
- [ ] Trip selection screen, large tap targets
- [ ] Last-trip persistence
- [ ] Long-lived session handling

---

# UC-T-02 · Télécharger le manifeste

| | |
|---|---|
| **Actor** | Controller |
| **Priority** | P0 · **Sprint** 5 · **Estimate** 3 days |
| **Criteria** | See UC-T-02, AC 1–6 |

**Goal.** Get everything needed for offline validation onto the device before leaving coverage.

### Main success scenario

1. Controller selects a trip.
2. System returns the signed manifest: every paid booking with reference, name, seat, code hash.
3. Client verifies the signature.
4. Manifest is stored in IndexedDB.
5. Screen shows passenger count and *« Manifeste prêt — mode hors ligne disponible »*.

### Alternate flows

- **A-1 · Re-download for late bookings.** Passengers buy up to 30 minutes before departure. Re-downloading merges new passengers **without erasing locally recorded boardings**. This merge is the subtle part — get it wrong and you lose boardings recorded before the refresh.
- **A-2 · Multiple trips cached.** A controller working two consecutive departures caches both.

### Exception flows

- **E-1 · Signature invalid.** Refuse the manifest entirely with a clear message and an instruction to contact the office. A tampered manifest could be used to board unpaid passengers.
- **E-2 · Download fails.** Retry; if a previous version is cached, offer to continue with it, clearly labelled with its age.
- **E-3 · Storage quota exceeded.** Evict manifests for trips that have already arrived.

### Business rules

- **BR-1** Contains code **hashes**, never plaintext codes. A stolen device must not yield working boarding codes.
- **BR-2** Signed server-side; verified client-side before any use.
- **BR-3** Under 10 seconds on 3G for 70 passengers — so send a lean payload, not the full booking objects.
- **BR-4** Merge on re-download preserves local boarding state. Test this explicitly.
- **BR-5** Manifests are purged 24 hours after arrival — passenger data should not accumulate on a shared device.

### Endpoints

`GET /api/v1/controller/trips/:id/manifest` → `{ trip, passengers[], signature, issued_at, expires_at }`

### Tasks

- [ ] Manifest endpoint with lean payload
- [ ] Server-side signing + client verification
- [ ] IndexedDB schema and write layer
- [ ] **Merge-on-redownload preserving local boardings**
- [ ] Quota management + purge of arrived trips
- [ ] 3G timing test with 70 passengers
- [ ] Invalid-signature refusal path

---

# UC-T-03 · Valider par scan du QR

| | |
|---|---|
| **Actor** | Controller |
| **Priority** | P0 · **Sprint** 5 · **Estimate** 4 days |
| **Criteria** | See UC-T-03, AC 1–6 |

**Goal.** Validate a passenger in under two seconds without a network.

### Main success scenario

1. Controller points the camera at the passenger's screen.
2. QR decodes; JWT signature verified locally.
3. Booking id checked against the cached manifest.
4. Not yet boarded → full-screen **green** with name and seat, held ≥ 2 seconds.
5. Boarding recorded locally.
6. Camera reopens automatically for the next passenger.

### Exception flows

- **E-1 · Camera permission denied.** Clear instructions to enable it, plus an immediate fallback to code entry (UC-T-04).
- **E-2 · Unreadable QR** (cracked screen, glare, low brightness). After 5 seconds, prompt *« Saisissez le code à 6 chiffres »*. This will happen often; make the fallback feel normal rather than like a failure.
- **E-3 · Expired token.** Red, *« Billet expiré »*.
- **E-4 · Valid signature, not in this manifest.** Red, naming the trip it belongs to.

### Business rules

- **BR-1** Verification is entirely local. No network call on the validation path, ever.
- **BR-2** Median scan-to-result under 2 seconds.
- **BR-3** Result screens are colour **plus** icon **plus** text — glare and colour-blindness both defeat colour alone.
- **BR-4** Green persists at least 2 seconds so the passenger sees it too. Validation is a moment of reassurance for them, not just a check for you.
- **BR-5** Auto-reopen the camera; a controller with 70 passengers must not tap between each one.
- **BR-6** Must work outdoors in direct sun and on a dim, cracked screen. Test both physically before closing this use case.

### Tasks

- [ ] Camera + QR decode library (evaluate on a low-end device before committing)
- [ ] Local JWT verification with embedded public key
- [ ] Manifest lookup from IndexedDB
- [ ] Full-screen result component (green / orange / red)
- [ ] Auto-reopen loop
- [ ] 5-second fallback prompt to code entry
- [ ] **Outdoor sunlight test; cracked-screen test**
- [ ] Scan latency measurement

---

# UC-T-04 · Valider par code à 6 chiffres

| | |
|---|---|
| **Actor** | Controller |
| **Priority** | P0 · **Sprint** 5 · **Estimate** 2 days |
| **Criteria** | See UC-T-04, AC 1–5 |

> Expect this path to carry more traffic than QR. Dead batteries, feature phones, broken screens and bright sunlight are all routine.

**Goal.** Validate a passenger who cannot show a QR.

### Main success scenario

1. Controller taps **Saisir le code**.
2. Large numeric keypad appears.
3. Types 6 digits.
4. bcrypt-compared against manifest hashes locally.
5. Match, not yet boarded → green result.
6. Keypad clears for the next passenger.

### Business rules

- **BR-1** Fully offline, like QR.
- **BR-2** Comparison under 1 second for 70 passengers on a low-end device. **bcrypt cost factor must be tuned for this constraint** — a cost factor chosen for server-side security will be far too slow here. Benchmark on the actual target device and pick the cost accordingly; if it cannot meet both requirements, consider a keyed hash rather than bcrypt for this path.
- **BR-3** Keypad buttons minimum 60 × 60 px. This is used one-handed, standing, in motion.
- **BR-4** Auto-submit on the sixth digit. No **Valider** button — it costs a tap per passenger.
- **BR-5** A visible backspace; mistyping is constant.

### Tasks

- [ ] Large numeric keypad component
- [ ] **Benchmark bcrypt cost on the target device against the 1-second budget**
- [ ] Offline hash comparison across the manifest
- [ ] Auto-submit on sixth digit
- [ ] Shared result component with UC-T-03
- [ ] 70-passenger timing test on a low-end phone

---

# UC-T-05 · Refuser un billet invalide ou déjà embarqué

| | |
|---|---|
| **Actor** | Controller |
| **Priority** | P0 · **Sprint** 5 · **Estimate** 2 days |
| **Criteria** | See UC-T-05, AC 1–5 |

**Goal.** Refuse clearly and safely — including when the controller is being watched by an unhappy passenger.

### Main success scenario

1. Validation attempt fails a check.
2. Full-screen result appropriate to the reason.
3. Controller taps to dismiss; scanner returns to ready.

### Business rules

- **BR-1** Three outcomes, three treatments:

| Outcome | Colour | Icon | Message |
|---|---|---|---|
| Already boarded | Orange | ⚠ | *Déjà embarqué à 05h42* |
| Wrong trip | Red | ✕ | *Billet pour DLA→BFS 08h00* |
| Not found | Red | ✕ | *Billet introuvable* |
| Cancelled / refunded | Red | ✕ | *Billet annulé* |

- **BR-2** "Already boarded" is **orange, not red**. It usually means an honest double-scan, not fraud, and the controller's tone toward the passenger should match. This is a small design decision with a real effect on how the product feels at the bus door.
- **BR-3** The original boarding time is shown for already-boarded — it lets the controller resolve the situation on the spot.
- **BR-4** A rejection never crashes or freezes the scanner. Verified by 50 consecutive rejections in a single session.
- **BR-5** Rejections are recorded locally and synced; a pattern of them at one agency is worth knowing about.
- **BR-6** No override. A controller cannot force-board an invalid ticket. Disputes go to the office by phone. Removing this temptation protects the controller as much as the system.

### Tasks

- [ ] Rejection reason classification
- [ ] Result variants with icon + colour + text
- [ ] Boarding-time display for duplicates
- [ ] Local logging of rejections
- [ ] **50-consecutive-rejection stability test**

---

# UC-T-06 · Fonctionner entièrement hors ligne

| | |
|---|---|
| **Actor** | Controller |
| **Priority** | P0 · **Sprint** 5 · **Estimate** 5 days |
| **Criteria** | See UC-T-06, AC 1–6 — **AC-4 is the gate** |

> Gares routières have poor coverage, buses board in concrete structures, and networks fail at exactly the busiest moments. Offline is not a degraded mode here; it is the normal mode.

**Goal.** Complete an entire boarding session with no network, losing nothing.

### Main success scenario

1. Manifest cached (UC-T-02).
2. Device loses connectivity — or is deliberately in airplane mode to save battery.
3. Every validation runs locally.
4. Each boarding is written to IndexedDB with a timestamp.
5. Offline indicator shows the pending-sync count.
6. On reconnection → UC-T-07.

### Exception flows

- **E-1 · Browser or tab killed mid-session.** Reopening restores all recorded boardings from IndexedDB. **Writes must be committed per boarding, not batched in memory** — this is the single most likely way to lose data, and AC-4 exists to catch it.
- **E-2 · Storage full.** Boardings take priority; evict old manifests.
- **E-3 · Device clock wrong.** Record both device time and a monotonic sequence number, so the server can reconstruct order even if timestamps are nonsense. Cheap phones frequently have wrong clocks.

### Business rules

- **BR-1** No network call on any validation path.
- **BR-2** Every boarding written to durable storage immediately, synchronously, before the green screen shows.
- **BR-3** Pending-sync count always visible — the controller must be able to see that nothing has been lost.
- **BR-4** Offline state is shown neutrally (*« Hors ligne — 23 embarquements en attente »*), not as an error. It is normal.
- **BR-5** A full 70-passenger session must fit within the battery budget with the camera active. Test on a phone at 30% charge.
- **BR-6** A monotonic sequence number accompanies every boarding, independent of the clock.

### Tasks

- [ ] Service worker + full offline app shell
- [ ] IndexedDB boarding store with **synchronous per-event commit**
- [ ] Monotonic sequence counter
- [ ] Offline indicator with pending count
- [ ] Storage quota strategy prioritising boardings
- [ ] **Kill-the-tab-mid-session recovery test**
- [ ] Battery test: 70 validations with camera active
- [ ] Clock-skew handling test

---

# UC-T-07 · Synchroniser les embarquements

| | |
|---|---|
| **Actor** | Controller + SYS |
| **Priority** | P0 · **Sprint** 5 · **Estimate** 4 days |
| **Criteria** | See UC-T-07, AC 1–6 |

**Goal.** Move offline boardings to the server without loss, duplication, or controller involvement.

### Main success scenario

1. Connectivity returns.
2. Client detects it and uploads pending boardings as one batch.
3. Server applies them, returning accepted and duplicate lists.
4. Client clears only what was accepted.
5. Indicator returns to *« Synchronisé »*.

### Exception flows

- **E-1 · Sync fails.** Exponential backoff, indefinite retry. **Never discard unsynced events** — the local copy is the only copy.
- **E-2 · Partial acceptance.** Only accepted events are cleared; the rest remain queued.
- **E-3 · Two devices boarded the same reference.** Earlier timestamp wins; the later is returned as a duplicate and flagged for the agency. Both are retained for investigation.
- **E-4 · Boarding for a trip already marked arrived.** Accept it anyway — late sync is expected — and flag it.

### Business rules

- **BR-1** Automatic. The controller never taps "sync"; they will forget, and then a phone gets lost.
- **BR-2** Idempotent: submitting the same batch three times produces one server state. Explicitly tested.
- **BR-3** **First boarding wins**, by device timestamp with the sequence number as tiebreaker.
- **BR-4** Conflicts are surfaced to the agency, not silently resolved. A duplicate boarding may indicate a real problem at the door.
- **BR-5** Local data is cleared only on confirmed server acceptance.
- **BR-6** Sync is one batch per trip, not one request per boarding — 70 requests over a weak connection will not complete.

### Endpoints

`POST /api/v1/controller/boardings/batch` → `{ accepted[], duplicates[] }`

### Tasks

- [ ] Connectivity detection + automatic sync trigger
- [ ] Batch upload endpoint, idempotent
- [ ] First-wins conflict resolution with sequence tiebreaker
- [ ] Duplicate flagging for the agency
- [ ] Exponential backoff, no-discard retry
- [ ] **Two-device overlapping-boarding test**
- [ ] **Same-batch-three-times idempotency test**

---

# UC-T-08 · Marquer le voyage comme parti

| | |
|---|---|
| **Actor** | Controller |
| **Priority** | P1 · **Sprint** 5 · **Estimate** 1 day |
| **Criteria** | See UC-T-08, AC 1–5 |

**Goal.** Produce the one trip-status signal the MVP has — without GPS.

### Main success scenario

1. Boarding complete; controller taps **Marquer comme parti**.
2. Confirmation prompt.
3. `trip.status='departed'` with timestamp, queued if offline.
4. Passengers looking up their ticket now see *Parti*.

### Business rules

- **BR-1** Confirmation required. An accidental tap that departs a bus 20 minutes early sends misleading SMS-adjacent status to every passenger.
- **BR-2** Works offline; queues like a boarding event.
- **BR-3** This is the **only** movement signal in the MVP. No GPS, no ETA, no map. Resist adding one — GPS is Phase 2 for good reasons documented in your founding paper.
- **BR-4** Departure time is recorded and becomes the raw material for the punctuality data you will eventually want. Start collecting it now even though nothing consumes it yet.
- **BR-5** A separate **Marquer comme arrivé** tap at destination, same mechanics.

### Tasks

- [ ] Departure/arrival action with confirmation
- [ ] Offline queueing alongside boardings
- [ ] `trip.status` transition + timestamp
- [ ] Status reflected in passenger lookup

---

# UC-A-01 · Consulter la journée de l'agence

| | |
|---|---|
| **Actor** | Agency manager |
| **Priority** | P0 · **Sprint** 6 · **Estimate** 3 days |
| **Criteria** | See UC-A-01, AC 1–6 |

> One screen. Every instinct will push you to add analytics, charts and history. Do not. The prototype's agency portal has six sections; the MVP has one.

**Goal.** Answer the manager's only real daily question: *what happened today, and does the cash match?*

### Main success scenario

1. Manager signs in (same PIN auth).
2. Sees today's trips: route, time, sold/capacity, revenue, status.
3. Sees agency totals: tickets, revenue, cash vs MoMo split.
4. Taps a trip → manifest (UC-A-02).

### Business rules

- **BR-1** Today by default; a simple date picker for past days. No date ranges, no comparisons, no charts.
- **BR-2** Figures reconcile exactly with the payment ledger. If the dashboard and the ledger ever disagree, every number you show becomes worthless to this manager.
- **BR-3** Cash / MoMo split is the headline figure — it is the answer to "how much should be in the drawer", which is the leakage question you are selling against.
- **BR-4** Loads in under 3 seconds on 3G.
- **BR-5** **Must work well on a phone.** The manager checks this from a car, from home, from another city. That mobility is precisely the value proposition.
- **BR-6** Per-cashier breakdown available on the day view — this is what makes leakage visible and is the feature the owner will actually talk about.

### Endpoints

`GET /api/v1/agency/today?date={date}`

### Tasks

- [ ] Daily aggregation endpoint
- [ ] Cash / MoMo split
- [ ] Per-cashier breakdown
- [ ] Trip list with occupancy and revenue
- [ ] Mobile-first layout
- [ ] Ledger reconciliation test

---

# UC-A-02 · Consulter et exporter un manifeste

| | |
|---|---|
| **Actor** | Agency manager |
| **Priority** | P0 · **Sprint** 6 · **Estimate** 3 days |
| **Criteria** | See UC-A-02, AC 1–4 |

**Goal.** Produce the paper manifest the agency's existing process still requires — and which the police at road checkpoints may ask for.

### Main success scenario

1. Manager selects a trip.
2. Sees the passenger list: seat, name, phone, channel, boarding status.
3. Taps **Exporter en PDF**.
4. A print-ready A4 PDF is produced.

### Business rules

- **BR-1** PDF must print legibly in black and white on a low-toner office printer. Print one before closing this use case — screen-designed PDFs routinely fail this.
- **BR-2** Includes agency name, route, date, departure, bus, driver if known, and passenger count. This is a semi-official document.
- **BR-3** Full phone numbers appear — the agency legitimately needs them — but the export is watermarked with the requesting user and timestamp, so an exported list that leaks is traceable.
- **BR-4** Boarding status shown as at generation time, with the generation timestamp printed.
- **BR-5** Sorted by seat number, not booking time. That is the order the controller walks the aisle in.
- **BR-6** Exports are logged. Under loi 2024/017 a bulk personal-data export is exactly the operation you must be able to account for.

### Endpoints

`GET /api/v1/agency/trips/:id/manifest` · `GET /api/v1/agency/trips/:id/manifest.pdf`

### Tasks

- [ ] Manifest view
- [ ] PDF generation, A4, print-optimised
- [ ] Watermarking with requester + timestamp
- [ ] Export audit logging
- [ ] **Physical print test on an office printer**

---

# UC-A-03 · Définir un horaire récurrent

| | |
|---|---|
| **Actor** | Agency manager or Operator |
| **Priority** | P0 · **Sprint** 1 · **Estimate** 4 days |
| **Depends on** | — (blocks everything; build first) |
| **Criteria** | See UC-A-03, AC 1–6 |

> Sequenced into **Sprint 1** despite being an agency feature: nothing else can be tested until trips exist.

**Goal.** Define departures once and have them exist indefinitely.

### Main success scenario

1. Manager opens **Horaires**.
2. Creates a template: route, bus, departure time, days of week, price, class.
3. Saves.
4. System immediately generates trips for the next 14 days.
5. Those departures become searchable (UC-P-01).

### Alternate flows

- **A-1 · Seasonal price change.** Edit the template's price; only future trips **with no bookings** are updated. Trips already carrying bookings keep their original price — repricing a sold ticket is not acceptable.
- **A-2 · Deactivate a template.** Generation stops; existing trips remain and must be cancelled individually via UC-A-04 if desired.
- **A-3 · One-off extra departure.** Create a single trip directly, without a template — festival periods and market days need this.

### Exception flows

- **E-1 · Bus double-booked.** Two templates assigning the same bus to overlapping windows are rejected at save time, naming the conflicting template. Without this check you will sell seats on a bus that is 200 km away.
- **E-2 · Editing a template with bookings.** Blocked for price and time; allowed for future-only fields. Explain clearly why.

### Business rules

- **BR-1** Templates generate; they do not own. Once a trip exists it is independent — this is what allows a single day to be cancelled without disturbing the schedule.
- **BR-2** Bus-conflict detection at save time, not at generation time. Failing silently at 02:00 in a cron job is how you discover the problem from a passenger.
- **BR-3** Price changes never affect trips with bookings.
- **BR-4** Times are Africa/Douala, stored UTC. Get this wrong and departures land on the wrong day near midnight.
- **BR-5** During the pilot the operator may configure templates on the agency's behalf — but build the screen anyway, because it is the first thing that stops scaling.

### Data

Writes `schedule_template`; triggers `trip` generation (UC-S-06).

### Endpoints

`POST/PATCH /api/v1/agency/schedules` · `POST /api/v1/agency/trips` (one-off)

### Tasks

- [ ] `schedule_template` table
- [ ] Template CRUD with validation
- [ ] Bus-conflict detection at save
- [ ] Immediate 14-day generation on save
- [ ] Price-change guard for booked trips
- [ ] One-off trip creation
- [ ] Timezone-correct day matching
- [ ] Schedule management screen

---

# UC-A-04 · Annuler un départ

| | |
|---|---|
| **Actor** | Agency manager |
| **Priority** | P1 · **Sprint** 6 · **Estimate** 3 days |
| **Criteria** | See UC-A-04, AC 1–6 |

**Goal.** Handle a breakdown or cancellation so that every affected passenger is told before they travel to the gare.

### Main success scenario

1. Manager selects the trip, taps **Annuler ce départ**.
2. Enters manager PIN, selects a reason.
3. Confirms — the passenger count is shown in the confirmation.
4. `trip='cancelled'`; it leaves public search immediately.
5. All affected passengers are SMSed within 5 minutes.
6. A refund task is created per paid booking.

### Business rules

- **BR-1** SMS within 5 minutes. A passenger who leaves for the gare before hearing from you is a passenger you have lost permanently.
- **BR-2** SMS names the reason and the refund timeline, and gives the WhatsApp number.
- **BR-3** Every paid booking gets a refund task — cash refunds handled at the counter, MoMo refunds by the operator.
- **BR-4** The manifest survives cancellation for records and disputes.
- **BR-5** Cancellation after departure is impossible; that is an incident, handled by phone.
- **BR-6** The confirmation dialog states how many passengers will be affected. It should feel weighty, because it is.

### Tasks

- [ ] Trip cancellation with manager PIN
- [ ] Bulk SMS dispatch to affected passengers
- [ ] Bulk refund task creation
- [ ] Immediate removal from search
- [ ] Passenger-count confirmation dialog
- [ ] Cancellation SMS template

---

# UC-O-01 · Intégrer une nouvelle agence

| | |
|---|---|
| **Actor** | Operator |
| **Priority** | P2 · **Sprint** 6 · **Estimate** 2 days |
| **Criteria** | See UC-O-01, AC 1–4 |

**Goal.** Bring an agency live in under an hour, without an admin UI.

**Deliberately manual.** With two agencies, building onboarding tooling is effort spent on a problem you do not have.

### Main success scenario

1. Operator collects agency details, bus inventory, routes, staff list.
2. Runs a documented seed script.
3. Verifies: a trip appears in search; a cashier can sign in; a test booking completes end to end.
4. Delivers PINs to staff in person.
5. Runs training (UC-O-01 is technical setup; training is a separate field activity).

### Business rules

- **BR-1** A written checklist exists and has been executed end-to-end at least once before the first real agency.
- **BR-2** PINs are delivered in person or by voice — never by SMS or email alongside the phone number.
- **BR-3** Seat numbering is confirmed against the physical bus during onboarding. Getting it wrong produces arguments at boarding that the cashier cannot resolve.
- **BR-4** An end-to-end test booking is completed and then cancelled before the agency goes live. Never let the first real booking be the first booking.
- **BR-5** Commission is set to 0% for the founder period, but the field exists from day one so enabling it later is a config change, not a migration.

### Tasks

- [ ] Seed script (agency, buses, routes, staff, templates)
- [ ] Written onboarding checklist
- [ ] End-to-end verification script
- [ ] PIN generation and secure delivery procedure

---

# UC-O-02 · Effectuer un remboursement

| | |
|---|---|
| **Actor** | Operator |
| **Priority** | P2 · **Sprint** 6 · **Estimate** 2 days |
| **Criteria** | See UC-O-02, AC 1–4 |

**Goal.** Return money reliably, and be able to prove you did.

**Deliberately manual.** Automated MoMo disbursement adds a second integration and a new class of failure. At pilot volumes, manual transfer plus a logged record is safer and faster to build.

### Main success scenario

1. Operator opens the refund task digest.
2. For each: verifies the original payment against the MoMo statement.
3. Executes a MoMo transfer to the payer's number.
4. Records the transfer reference against the task.
5. Task → `completed`; passenger SMSed.

### Business rules

- **BR-1** Refunds execute within 24 hours of task creation. Measured, not assumed.
- **BR-2** Every refund carries an authorising person and a reason in `audit_log`.
- **BR-3** The original payment is verified against the provider statement before transferring — refunding a payment that never succeeded is an easy and expensive mistake.
- **BR-4** Refund tasks appear in a daily digest that is actually read, not a table someone must remember to check.
- **BR-5** A weekly query lists outstanding tasks; anything over 24 hours old is escalated.
- **BR-6** Refunds go to the **payer's** number, not the traveller's — they may differ, and this is exactly the diaspora-adjacent case.

### Tasks

- [ ] `refund_task` table + statuses
- [ ] Daily digest to the operator
- [ ] Manual completion with transfer reference capture
- [ ] Refund confirmation SMS
- [ ] Weekly outstanding-task query + escalation

---

## Sprint summary — Part 2

| Sprint | Use cases | Estimated developer-days |
|---|---|---|
| 1 (pulled forward) | A-03 | 4 |
| 4 — Counter | C-01 … C-07 | 19 |
| 5 — Controller | T-01 … T-08 | 23 |
| 6 — Launch | A-01, A-02, A-04, O-01, O-02 | 13 |

**Part 2 total: ~59 developer-days.**

## Combined estimate

| | Developer-days |
|---|---|
| Part 1 (passenger + system) | 49 |
| Part 2 (staff applications) | 59 |
| **Subtotal** | **108** |
| Integration, review, and unestimated work (+30%) | 32 |
| **Total** | **~140 developer-days** |

Which is roughly **28 working weeks for one developer**, or **14–16 weeks for two** working in parallel with a clean split — one owning payments and system jobs, the other owning the passenger and staff interfaces.

This is meaningfully more than the 10–14 weeks in your v2 founding document. That estimate was not unreasonable for the five functions as written there; the difference is counter sales, which were not in the original MVP, plus the real cost of the payment edge cases that a mockup makes invisible.

Two honest options if the timeline matters more than the scope:

**Option A — cut the controller app from launch (saves ~23 days).** Board with a printed manifest for the first month, exactly as agencies do today. You lose the boarding-time data and some of the demo appeal, but nothing structural. Add the scanner in month two once tickets are actually selling. This is the cut I would make.

**Option B — cut counter sales (saves ~19 days).** I would not. It reintroduces the split-inventory failure, and it removes the reason the agency wants the product at all.
