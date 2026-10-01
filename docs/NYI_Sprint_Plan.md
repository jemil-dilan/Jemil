# NYI — MVP Sprint Plan

Source of truth: `NYI_MVP_Specification_Document_v1.1.docx`
Every item below references a use case ID from that document (UC-xx = human-triggered, SYS-xx = system/automation, ONB-xx = onboarding). No sprint should implement anything that isn't traceable to a UC/SYS/ONB ID in the spec. If a task doesn't map to one, it doesn't belong in this MVP — flag it and put it in the backlog instead.

Each sprint below is a unit of deployable value: by the end of a sprint, something real and demoable should work end-to-end, not just "code exists." Sprints are ordered by implementation dependency — do not start a later sprint before its dependencies are done.

---

## Sprint 0 — Rebrand JEMIL → NYI (prerequisite, do this first)

**Goal:** the project is completely and consistently renamed before a single line of MVP feature code is written. The project was originally called JEMIL (the founder's own name, used as a placeholder) and is now permanently NYI, "Le Voyage, Autrement." Nothing from Sprint 1 onward should ever reference the old name.

**Use cases:** none — this is a prerequisite housekeeping task, not a product use case. It exists to prevent the old name from leaking into code, docs, configs, or anything user-facing once real implementation starts.

**Steps:**
1. Repository & project metadata: rename the repository itself (or create the new one and migrate), package/module names, `package.json` / `pom.xml` / `pyproject.toml` (whichever applies) project name field, README title, and any CI/CD pipeline names referencing JEMIL.
2. Codebase-wide search for "JEMIL" / "Jemil" / "jemil" (case-insensitive) across source files, config files, environment variable names, database names, and comments. Replace with "NYI" / "Nyi" / "nyi" as appropriate to each context — do not do a blind find-and-replace without checking each hit makes sense (e.g. `JEMIL_API_KEY` → `NYI_API_KEY`, not something awkward).
3. Database: rename the database/schema if it's currently named after JEMIL; if already in use with data, this needs a migration script, not a manual rename — write it, don't hand-edit.
4. Branding assets: replace any existing JEMIL logo, favicon, color tokens, or app name strings with NYI equivalents (the NYI logo, wordmark "NYI — Le Voyage, Autrement," and color palette already exist per the founder-provided assets).
5. Domain/hosting: update any deployed environment's app name, subdomain, or hosting project name if one was already provisioned under "jemil."
6. External-facing config: any SMS sender ID, email sender name, or payment provider merchant display name currently set to JEMIL must be updated to NYI (this may require re-registering the sender ID with the SMS/MoMo provider — check lead time, this can take days with some providers).
7. Documentation sweep: confirm all prior founding documents, the spec, and this sprint plan are filed under the NYI name only going forward; archive old JEMIL-named documents rather than deleting them (they're historical record, not active references).
8. Final verification: run a full-repository case-insensitive search for "jemil" and confirm zero remaining hits outside of an explicitly labeled historical/archive folder.

**Dependencies:** none — this is the first task, before Sprint 1.

**Definition of Done:**
- A case-insensitive repository-wide search for "jemil" returns no hits outside an archive folder.
- Any provisioned infrastructure, database, or external sender identity reflects NYI, not JEMIL.
- No new code written from Sprint 1 onward ever reintroduces the old name.

---

## Sprint 1 — Foundations & Onboarding

**Goal:** the platform can bootstrap itself — an Operator, a Branch, and staff accounts can exist before any trip or sale is possible.

**Use cases:**
- ONB-01 — Onboard Operator and first Branch
- ONB-02 — Onboard staff (create User + membership)
- ONB-03 — Seed catalog (Cities & Routes)

**Steps:**
1. Implement `identity/auth` module: staff authentication only (JWT/OIDC), no passenger accounts.
2. Implement `organization` module: Operator, Branch, `UserBranchMembership` (userId + branchId + role + permissions + status).
3. Implement ONB-01 flow: create Operator (legal name, license number, contact, commission rate, status), create first Branch, link them, record commercial terms.
4. Implement ONB-02 flow with the hierarchy rules: NYI Admin → creates Operator HQ users; Operator HQ → creates Branch Managers / Operations Managers; Branch Manager → creates Cashiers / Controllers. Deactivation must immediately block access.
5. Implement ONB-03: Cities and Routes CRUD, link Operator to Routes it operates.
6. Implement scope enforcement at API level (BRANCH / OPERATOR / PLATFORM) — write tests that prove a Branch Manager cannot query another branch's data.
7. Implement the audit module skeleton now (actor, timestamp, action, before/after) — every module built after this sprint must write to it.

**Dependencies:** Sprint 0 (rebrand must be complete — no module here should be scaffolded under the old name).

**Definition of Done:**
- An Operator + Branch + one user per role can be created end-to-end via API.
- A deactivated membership blocks login immediately.
- Audit entries exist for every creation above.

---

## Sprint 2 — Trip Catalog & Fleet Assignment

**Goal:** a trip can be created, opened for sale, and staffed with a bus and driver.

**Use cases:**
- UC-07 — Create and open a Trip
- UC-08 — Assign bus and driver to a trip

**Steps:**
1. Implement `catalog` module consumption: Route selection (origin/destination).
2. Implement Trip aggregate with lifecycle states: DRAFT → SCHEDULED → OPEN_FOR_SALE → BOARDING → DEPARTED → IN_TRANSIT → ARRIVED → CANCELLED. Enforce allowed transitions only in the domain layer.
3. UC-07: create Trip (route, departure branch, date/time, capacity, base price), transition to OPEN_FOR_SALE only when ready. A Trip must not accept bookings before this state.
4. Implement `fleet` module: Bus (registration, capacity, category, documents, status) and Driver (identity, license, status) as Operator-owned assets.
5. UC-08: assign Bus (capacity compatibility check) and Driver to a Trip. Record assignment with timestamp + actor. Bus is never permanently tied to a Driver — assignment always happens at Trip level, and replacing either creates a history entry, not a deletion.

**Dependencies:** Sprint 1 (Operator/Branch/Routes must exist).

**Definition of Done:**
- A Trip can move DRAFT → OPEN_FOR_SALE and is then visible to search.
- Reassigning a bus or driver preserves history and produces an audit entry.

---

## Sprint 3 — Booking Engine & Seat Inventory

**Goal:** a seat can be searched, held, and reliably protected from double-booking, with automatic expiry.

**Use cases:**
- UC-01 — Search available trips
- UC-02 — Reserve a seat (online)
- SYS-01 — Seat hold expiry (automation)

**Steps:**
1. Implement `booking` module: Booking aggregate with states HELD → PENDING_PAYMENT → CONFIRMED → ISSUED → CHECKED_IN → BOARDED → COMPLETED, plus CANCELLED / REFUNDED / NO_SHOW / EXPIRED.
2. UC-01: search by origin/destination/date, return only OPEN_FOR_SALE trips with live seat availability.
3. UC-02: create Booking in HELD state with short TTL (10–15 min). Seat inventory must be protected by transactional locking or optimistic concurrency — write a concurrency test that proves two simultaneous requests for the same seat cannot both succeed.
4. Implement `jobs` module and SYS-01: background job (e.g. every 60s) that expires HELD bookings past TTL and releases the seat.
5. **Critical:** implement the SYS-01 race condition rule explicitly — if a payment confirmation arrives at the exact moment of expiry, payment wins. The expiry job must re-check booking state under the same transactional lock before expiring it. Write a test that simulates this race and asserts the booking survives.
6. Job must be idempotent (safe to re-run, only processes still-HELD expired rows) and must alert if the number of expirations in one run exceeds a sane threshold.

**Dependencies:** Sprint 2 (Trips must exist and be OPEN_FOR_SALE).

**Definition of Done:**
- No seat can be double-held or double-sold under concurrent load (proven by an automated concurrency test, not just manual testing).
- A booking survives if payment confirms during the expiry window.
- Expired holds release the seat within one job cycle.

---

## Sprint 4 — Payments (MTN MoMo) & Reconciliation

**Goal:** a booking can be paid for via MoMo, and no booking is ever left permanently stuck regardless of webhook reliability.

**Use cases:**
- UC-03 — Pay with MTN Mobile Money
- UC-12 — Diaspora purchase for a relative (Buyer ≠ Passenger)
- SYS-02 — Payment reconciliation / webhook recovery (automation)

**Steps:**
1. Implement `payment` module with the MoMo integration fully isolated behind a port/adapter (domain code must never import a provider-specific type).
2. **Before writing code:** confirm with the chosen MoMo integration path (direct MTN partnership vs. an aggregator such as Maviance/HUB2, Flutterwave, NotchPay, etc.) whether it exposes (a) a payment-status polling endpoint and (b) a refund endpoint. This determines whether SYS-02 and the future SYS-03 can be fully automated or require a manual-intervention step. Document the answer in the module's README before proceeding.
3. UC-03: initiate MoMo payment request, handle callback, transition Booking PENDING_PAYMENT → CONFIRMED → ISSUED. Callback handling must be idempotent (duplicate callbacks must not double-process).
4. UC-12: separate Buyer and Passenger as distinct fields on Booking — a Buyer can pay while a different Passenger travels. Confirmation can be sent to the passenger's phone. Passenger still requires no account.
5. SYS-02: background job selects Bookings stuck in PENDING_PAYMENT past N minutes (configurable, e.g. 8–12 min), calls the MoMo status endpoint, and resolves to CONFIRMED or EXPIRED/CANCELLED accordingly. Retry with backoff on provider unavailability; only give up after a max age (e.g. 30–45 min), then release the seat and raise a support-queue alert.
6. Add a DB-level guard (unique constraint or conditional update) on the PENDING_PAYMENT → CONFIRMED transition so a webhook and a poll firing near-simultaneously cannot both "win" and double-process the same booking.

**Dependencies:** Sprint 3 (Booking/HELD state must exist).

**Definition of Done:**
- A passenger who actually paid is never told their booking expired (tested by simulating a delayed webhook).
- A passenger who didn't pay never keeps a seat indefinitely.
- All payment state transitions are audited with the provider's response snapshot attached.

---

## Sprint 5 — Ticket Issuance & Notification Reliability

**Goal:** a confirmed booking reliably becomes a usable travel document, even under SMS provider failure, and can always be recovered later.

**Use cases:**
- UC-04 — Issue ticket (QR + SMS)
- SYS-04 — SMS delivery failure / retry (automation)
- UC-14 — Passenger ticket lookup / resend

**Steps:**
1. Implement QR generation: signed, single-use or time-bound token (JWT or equivalent) plus a short alphanumeric fallback code that works without internet access on the passenger's side.
2. Implement `notification` module with SMS adapter behind a port. On ISSUED, send SMS with trip summary + code.
3. SYS-04: on send failure or no delivery receipt within timeout, schedule retries (e.g. 3 attempts with backoff). After final failure, mark FAILED and raise a support/ops alert — the ticket remains valid regardless (QR still exists server-side).
4. Add a cutoff rule to SYS-04: stop retrying once the trip has reached BOARDING status — a late SMS at that point has no value and shouldn't keep consuming retry budget.
5. UC-14: implement lookup by phone number + reference (or last 4 digits + travel date). Rate-limit this endpoint to prevent enumeration/abuse. Only returns tickets matching all provided identity factors. Staff-performed lookups are audited.

**Dependencies:** Sprint 4 (Booking must reach CONFIRMED/ISSUED).

**Definition of Done:**
- A ticket issued with a failed SMS is still fully recoverable via UC-14.
- SMS failure rate is visible in metrics (see Sprint 10).
- QR cannot be replayed after single use (or after its time-bound validity expires).

---

## Sprint 6 — Counter Sales (Guichet) & Cash Reconciliation

**Goal:** a cashier can sell the exact same inventory as the online channel, and end their shift with a reconciled, auditable cash session.

**Use cases:**
- UC-05 — Counter sale (guichet)
- UC-06 — Open / close cash session

**Steps:**
1. Reuse the Sprint 3–5 booking/payment/ticketing engine for the counter channel — **do not build a parallel booking path.** The acceptance test for this sprint should prove that an online booking and a counter booking for the same trip cannot both claim the same seat.
2. Implement `CashRegisterSession` aggregate: OPEN → ACTIVE → CLOSING → CLOSED, with PENDING_APPROVAL / APPROVED / REJECTED as the supervisor branch.
3. UC-05: cashier must have an open session before any sale; every counter sale links to that session; payment method can be cash or MoMo (cash payments confirm immediately, no adapter needed).
4. UC-06 open flow: record opening float, cashier, branch, timestamp.
5. UC-06 close flow: system computes expected balance (sales − refunds + opening) — never accept a manually typed total. If |variance| ≤ threshold → CLOSED directly. If |variance| > threshold → PENDING_APPROVAL, notify the Branch Manager, who reviews and marks APPROVED or REJECTED. A rejected session must not silently close — it stays open or escalates.

**Dependencies:** Sprint 5 (full booking/payment/ticket engine must exist).

**Definition of Done:**
- No sale is possible outside an open session for that cashier.
- A significant variance cannot be closed without explicit supervisor action.
- A cashier only ever sees their own branch's data.

---

## Sprint 7 — Controller, Boarding & Manifest (Offline-First)

**Goal:** a controller can validate tickets and board passengers with or without network connectivity, and the manifest is always derived automatically, never retyped.

**Use cases:**
- UC-09 — Control & boarding (online + offline)
- SYS-05 — Manifest derivation (automation)
- UC-10 — Generate manifest

**Steps:**
1. SYS-05: manifest snapshot job/query — selects all tickets of a trip in ISSUED or later non-cancelled states, ordered by seat/booking time. Manifest must never be manually rebuilt from cash totals.
2. UC-10: expose the manifest to Controller/Branch Manager, exportable as PDF or simple print view. Shows seat, passenger name, phone, boarding status.
3. UC-09 online flow: Controller opens manifest, scans QR or searches by reference/SMS code, system marks CHECKED_IN then BOARDED. Duplicate scan of an already-boarded ticket must be detected and rejected, not silently accepted.
4. UC-09 offline flow: implement device model (deviceId, associated user + branch, lastSyncAt, revocation support). Controller downloads the manifest snapshot in advance; validation runs against the cached list locally; each offline validation event gets a unique ID for deduplication; results queue locally and sync when connectivity returns.
5. Define and test the offline conflict-resolution rule explicitly (e.g. what happens if the same ticket was somehow validated by two different offline devices before sync — the spec requires this to be defined and tested before production, not left implicit).

**Dependencies:** Sprint 5 (tickets must be ISSUED). Can be built in parallel with Sprint 6.

**Definition of Done:**
- Same ticket cannot be boarded twice without detection, online or offline.
- A controller with zero connectivity can complete a full boarding cycle and sync cleanly once reconnected.
- Manifest always matches issued tickets exactly — no manual entry path exists.

---

## Sprint 8 — Cancellation, Mass Refund & Manual Refunds

**Goal:** cancelling a trip or refunding a ticket is safe, automatic where possible, and never leaves money or seats in an undefined state.

**Use cases:**
- UC-11 — Basic refund (controlled)
- SYS-03 — Automatic mass refund on trip cancellation (automation)

**Steps:**
1. UC-11: implement permissioned refund (Cashier or Branch Manager only, per role permissions), linked to the original payment, fully audited. Seat released if rules allow.
2. SYS-03: on Trip → CANCELLED, select all tickets in ISSUED/CONFIRMED/CHECKED_IN for that trip. For each, create a Refund in PENDING and call the payment provider's refund path (see Sprint 4, step 2 — if no programmatic refund endpoint exists, this step instead creates a pending refund record and raises it to the ops queue for manual execution, rather than failing silently).
3. On refund success: ticket → REFUNDED, notify passenger by SMS. On individual failure: mark that one refund FAILED and queue for retry/manual resolution — never block the whole batch on one failure.
4. Produce a cancellation report for the operator: count, amounts, failures — visible to Branch Manager / Operator HQ.

**Dependencies:** Sprint 4 (payment/refund adapter), Sprint 5 (notifications).

**Definition of Done:**
- No ISSUED ticket on a CANCELLED trip is left without a refund attempt.
- Staff do not need to refund ticket-by-ticket by default.
- Target: majority of refunds resolved within 2 hours under normal provider conditions (or flagged to ops immediately if the provider has no automated refund path).

---

## Sprint 9 — Scoped Dashboards & Reporting

**Goal:** every role sees exactly the operational data their scope entitles them to, and nothing more.

**Use cases:**
- UC-13 — Scoped dashboards

**Steps:**
1. Branch dashboard: today's trips, sales, occupancy, cash session status, basic incidents — BRANCH scope only.
2. Operator dashboard: consolidation across all branches — OPERATOR scope.
3. Enforce scope at the query layer, not just the UI layer — write a test that attempts to fetch another branch's data directly via API and confirms it's rejected, not just hidden in the interface.
4. Basic metrics surfaced here: sales, payment success rate, job success rate (Sprints 3/4), SMS failure rate (Sprint 5), cash variance rate (Sprint 6).

**Dependencies:** Sprints 1–8 (needs real data flowing through the system to be meaningful).

**Definition of Done:**
- A Branch Manager cannot see another branch's data under any circumstance, proven by an automated test, not manual inspection.

---

## Sprint 10 — Hardening, Compliance & Pilot Launch

**Goal:** the system is production-safe and legally compliant enough to run a real pilot with a real operator.

**Steps:**
1. Audit trail completeness review: confirm every sensitive action across all prior sprints (sales, refunds, assignments, cancellations, cash close, staff creation/deactivation) writes a complete audit entry (actor, timestamp, action, before/after).
2. Data protection compliance pass against Cameroon Law n°2024/017 (in force since June 2026): lawful basis for processing passenger name/phone, retention policy, access rights, secure storage. Produce the data-processing note referenced in the spec's NFR section. This is an internal readiness note, not a substitute for actual legal review — flag to the founder that a lawyer should still sign off before real passenger data flows at scale.
3. Observability pass: structured logs, job success/failure rates, SMS failure rate, payment success rate, error tracking, alerting on job failures and high-variance cash sessions.
4. Security pass: least privilege per role re-verified, secrets confirmed outside source code, HTTPS-only confirmed in the deployment target.
5. Run the full Global Acceptance Checklist from the spec document (section 10) end-to-end against the pilot corridor (Douala–Yaoundé) with one real operator and at least two branches.
6. Go/no-go review before pilot: does every item in the checklist pass? If not, it blocks launch — it does not get waived "for now."

**Dependencies:** all prior sprints.

**Definition of Done:**
- Every item in the spec's Global Acceptance Checklist (§10) passes against a real operator, not a test fixture.
- Pilot go-live approved explicitly by the founder, not assumed.

---

## Full Traceability Table

| Sprint | Use cases covered |
|---|---|
| 0 | Full rebrand JEMIL → NYI (prerequisite, no use case ID) |
| 1 | ONB-01, ONB-02, ONB-03 |
| 2 | UC-07, UC-08 |
| 3 | UC-01, UC-02, SYS-01 |
| 4 | UC-03, UC-12, SYS-02 |
| 5 | UC-04, SYS-04, UC-14 |
| 6 | UC-05, UC-06 |
| 7 | UC-09, UC-10, SYS-05 |
| 8 | UC-11, SYS-03 |
| 9 | UC-13 |
| 10 | Hardening / compliance / launch readiness (no new UC) |

If any implementation task doesn't map to a row in this table, stop and check it against the spec's §3.1 MVP scope before proceeding — per the spec's Final Directive, anything not listed goes to the backlog, not into the sprint.
