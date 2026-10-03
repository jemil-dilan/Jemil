# NYI — Implementation Guideline

**Read this file in full before starting work on any task, sprint item, or use case.** It applies to every task in `NYI_Sprint_Plan.md`, with no exceptions. Skipping a step here is not a shortcut — it's the fastest way to reintroduce the exact bugs the spec was written to prevent (double bookings, silent scope creep, un-auditable actions, untested race conditions).

This guideline has four mandatory phases, in order: **Status Analysis → Plan & Approval → Git Setup → TDD Implementation.** Do not begin writing implementation code before Phase 4, and do not begin Phase 4 before the plan in Phase 2 has been explicitly approved.

Phase 1 carries a rule that is easy to skim past and expensive to get wrong, so it is stated here too: **check what has already been done before you build on it, and never modify previously completed work without stating why and getting explicit approval for that specific change.** Verifying a foundation is not the same as being entitled to change it. Approval of a plan is not approval to modify another task's work — that is a separate decision, asked and answered on its own. Equally, do not reach into a sprint or task you do not own to make the current one look demonstrable.

Phase 3 assumes `main` is a truthful baseline. If the branch you are told to build on contradicts `status.md`, resolve that before starting, not after the first commit.

---

## Phase 1 — Analyze Current Project Status

Before proposing any plan, you must understand the actual, current state of the codebase — not the state you assume it's in from memory of a previous session.

1. Analyze the project as it currently exists: which modules exist, which use cases from the spec are actually implemented (not just scaffolded), what tests exist and pass, what's incomplete or stubbed.
2. Check whether `status.md` already exists at the project root.
   - **If it does not exist:** create it from your analysis (template below).
   - **If it exists:** read it fully, then compare it against your own fresh analysis of the codebase. `status.md` is a snapshot written after a previous task — it may be stale, incomplete, or simply wrong if it wasn't updated after the last change. Do not trust it blindly. Flag every discrepancy you find between what it claims and what the code actually shows.
3. Update `status.md` to reflect reality, and explicitly note any discrepancies you just resolved (e.g. "status.md claimed UC-02 was complete; concurrency test was missing — corrected below").
4. **Check what has already been done, and confirm the task at hand is actually entitled to build on it.** Do this before proposing any plan, at both levels that matter:
   - **Sprint level:** is every prior sprint the current one depends on genuinely complete, per its own Definition of Done? "Mostly implemented" is not done.
   - **Step level, inside the current sprint:** are the *earlier steps of the sprint you are working in* actually implemented, or only scaffolded? A step marked "not started" is not a foundation you may lean on, and a step marked "in progress" may be someone else's unfinished work. Read the steps in order — an out-of-order step is not automatically unblocked just because its table or module happens to exist.
   - **Verify by reading the code or running the tests.** Never infer completion from a status file, a commit message, a branch name, or your own memory of an earlier session. A commit whose message says the tests pass is a claim, not evidence. If the branch you would build on is unmerged, that is an open question to resolve, not a detail to skip.
5. **If the task requires modifying, undoing, or building on work that an earlier task already completed, you must state why the change is necessary and get explicit user approval for it before making any change.** This holds even when the change is obviously correct, when the earlier work is unmerged, when it is only a rename, and when it is a single line. It also covers work that is *incomplete but committed* — finishing, renaming, or reinterpreting another task's half-done change is a modification of that task, not a new task.
   - Present the specific change, the concrete reason it is needed, what it would break, and what happens if it is deferred. A change can be necessary and still be worth declining.
   - Approval of the plan as a whole is **not** approval to modify previously completed work. It must be asked for separately and given on its own. Do not infer it from silence, from an unrelated follow-up, or from agreement with an unrelated part of the plan.
6. **Do not pull another sprint's or another task's work into the current task** in order to make it demonstrable or testable. Choosing what to instrument, migrate, or refactor is part of the task's scope, and it belongs to whichever task owns that code. Reaching into a later sprint's use cases to produce a realistic-looking test is scope creep even when the code change is small and even when the test genuinely needs it. If a realistic test seems to require touching code the current task does not own, that is a signal to split the task or to agree the dependency explicitly — not to proceed.

### `status.md` template

```markdown
# NYI — Project Status

Last updated: <date> by <task reference, e.g. working/NYI-014>

## Sprint progress
| Sprint                       | Status                           | Notes |
|------------------------------|----------------------------------|-------|
| 0 — Foundations & Onboarding | Done / In progress / Not started |       |
| 1 — Trip Catalog & Fleet     | ...                              |       |
| ...                          | ...                              |       |

## Use case status
| ID     | Implemented | Unit tested | E2E tested | Notes |
|--------|-------------|-------------|------------|-------|
| ONB-01 | Yes/No      | Yes/No      | Yes/No     |       |
| UC-01  | ...         | ...         | ...        |       |
| SYS-01 | ...         | ...         | ...        |       |

## Known gaps / discrepancies found this session
- <anything status.md previously claimed that didn't match reality>

## Architecture notes relevant to next task
- <module boundaries, adapters in place, anything the next task must respect>
```

---

## Phase 2 — Propose a Plan and Get Approval

**Do not start implementing until this phase is explicitly approved.** This is a hard stop, not a formality.

1. Based on the status analysis and the relevant sprint/use-case entry in `NYI_Sprint_Plan.md`, propose a detailed, precise implementation plan for the specific task at hand.
2. The plan must explicitly follow the architecture already in place (hexagonal/clean architecture, modular monolith, the module boundaries defined in the spec's §8.2 — `organization`, `catalog`, `fleet`, `trip`, `booking`, `payment`, `cash`, `boarding`, `notification`, `jobs`, `reporting`, `audit`). If the task seems to require deviating from this architecture, say so explicitly and explain why, rather than quietly doing something different.
3. The plan should state: which use case(s) it implements, which files/modules it will touch, what new states/transitions/aggregates it introduces, what the Cucumber scenario(s) will cover, and what could break in adjacent modules.
4. Present the plan and **wait for explicit approval before proceeding.** Do not interpret silence or an unrelated follow-up message as approval.

---

## Phase 3 — Git & Task Tracking Setup

Only begin this phase after the plan is approved.

1. Ensure you are on the main branch. If not, switch to it.
2. Update main (pull latest changes).
3. Create a new branch from main named exactly: `working/NYI-<task-number>`
4. Create a task file at the project root (e.g. `TASK_NYI-<task-number>.md`) that states clearly, as a checklist, everything the approved plan says will be implemented — one line per concrete step.
5. As each step is completed during implementation, mark it done in this file. This file is the running, visible record of progress on this specific task — keep it accurate in real time, not retroactively.
6. Once the task is fully complete (implementation done, tests passing, e2e green — see Phase 4), **delete this task file** before considering the task finished. It is a working artifact for the duration of the task only, not a permanent record (that's what `status.md` and the commit history are for).

### Task file template

```markdown
# Task NYI-<number>: <short title>

Plan approved: <date>
Use case(s): <UC-xx / SYS-xx / ONB-xx>
Branch: working/NYI-<task-number>

## Steps
- [ ] Write Cucumber feature file(s) for <scenario>
- [ ] Implement <component/module>
- [ ] Write unit tests for <method/class>
- [ ] Wire adapter/port for <external dependency, if any>
- [ ] Run full e2e suite
- [ ] Confirm all acceptance criteria from the spec are met
```

---

## Phase 4 — TDD Implementation Workflow

Follow this order strictly. Do not write implementation code before its corresponding e2e test exists.

1. **Write the Cucumber (Gherkin) end-to-end test(s) first**, directly from the use case's acceptance criteria in the spec. The scenario must reflect the real environment as accurately as possible — realistic data shapes, realistic timing for time-dependent behavior (holds, job intervals, retry backoffs), and realistic failure modes of external dependencies (e.g. a MoMo callback arriving late, an SMS provider timing out), not simplified happy-path-only mocks. If the use case has a documented race condition or failure path in the spec (e.g. SYS-01's payment-vs-expiry race), it must have its own scenario — not be left implicit.
2. **Implement the code**, module by module, following the approved plan and existing architecture boundaries. Do not introduce a new module or bypass an existing port/adapter without flagging it and getting it approved first.
3. **Write unit tests alongside each method as it's written** — not in a batch afterward. Every piece of business logic (state transitions, validation rules, calculations such as cash-session variance) needs a unit test proving it in isolation, independent of the e2e test.
4. **Run the e2e test suite** once implementation and unit tests are in place. All scenarios — including the failure-path and race-condition scenarios from step 1 — must pass before the task is considered complete.
5. If an e2e test fails, fix the implementation, not the test — unless the test itself was wrong, in which case say so explicitly and explain why before changing it.
6. Only after the full e2e suite is green: mark the final steps done in the task file, then delete the task file per Phase 3, step 6.

### What "reflects the real environment" means in practice

- Test against sandbox/staging versions of real external dependencies (MoMo sandbox, real SMS gateway sandbox) wherever one exists, rather than a hand-rolled fake that only returns success.
- Include at least one scenario per use case where the external dependency behaves badly (timeout, delayed callback, malformed response) — this MVP's entire premise is reliability under exactly these conditions.
- Timing-sensitive scenarios (hold expiry, reconciliation job, retry backoff) should exercise actual elapsed time or a controlled clock, not just call the function directly and assume timing is irrelevant.
- Concurrency-sensitive scenarios (seat holds, cash session close) must include a scenario with genuinely simultaneous requests, not sequential calls that happen to be near each other in the test file.

---

## Summary checklist (pin this)

- [ ] Analyzed current project state; `status.md` created or reconciled against reality
- [ ] Confirmed the task's real dependencies are complete, at sprint level *and* step level, verified against code/tests rather than claimed by a status file or commit message
- [ ] Any modification to already-completed work was stated, justified, and explicitly approved as its own decision — not folded into plan approval
- [ ] No other sprint's or task's code was pulled in to make this task demonstrable
- [ ] Plan proposed, follows existing architecture, explicitly approved before coding started
- [ ] On main, updated, new `working/NYI-<task-number>` branch created
- [ ] Task file created and kept current throughout
- [ ] Cucumber e2e test(s) written first, including failure/race-condition scenarios
- [ ] Implementation + unit tests written together, module by module
- [ ] Full e2e suite run and green, reflecting real-environment conditions
- [ ] Task file deleted after completion
- [ ] `status.md` updated to reflect the newly completed work, ready for the next task's Phase 1
