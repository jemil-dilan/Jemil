# Archive

These documents are **historical record only**. They were written under the
project's previous name and describe sprints and specifications that have since
been superseded. They are kept here, unmodified, for two reasons:

1. **Audit trail.** They record what was decided, when, and on what basis.
   Deleting them would destroy the reasoning behind the current codebase.
2. **Provenance.** Where the current specification and these documents disagree,
   the current specification wins.

## Do not use these as a source of truth

Everything here predates the NYI rebrand (Sprint 0, task `NYI-1`) and the MVP
specification v1.1. In particular:

| Archived document | Superseded by |
|---|---|
| `JEMIL_MVP_Build_Spec.md` | [`../NYI_MVP_Specification_Document_v1.1.docx`](../NYI_MVP_Specification_Document_v1.1.docx) |
| `JEMIL_Use_Cases_Acceptance_Criteria.md` | §use cases and acceptance criteria in the MVP specification v1.1 |
| `JEMIL_UC_Detail_Part1_Passenger_System.md` | as above |
| `JEMIL_UC_Detail_Part2_Staff_Apps.md` | as above |
| `JEMIL_Backend_Gap_Analysis.md` | the current gap analysis in the repository root `status.md` |
| `STATUS.md` | the current `status.md` in the repository root |
| `Sprint-1-Retrospective.md` | nothing — a completed sprint's retrospective, kept for the record |

## What is *not* archived

`../deployment/Sprint-1-Deployment-Guide.md` stayed a live operational document.
It contains runnable commands, so it was renamed to NYI rather than archived.

## Why the retired name is still in this folder

The retired project name necessarily appears throughout these files — in their
filenames and their prose. That is the point of an archive. The acceptance gate
[`../../scripts/verify_rebrand.sh`](../../scripts/verify_rebrand.sh) permits it
here and only here.

The same allowance applies to two other deliberate exceptions, neither of which
is in this folder:

- **Liquibase `<changeSet>` tags** in `src/main/resources/db/changelog/*.xml`.
  Liquibase identifies a changeset by the `(id, author, filename)` triple, so
  renaming an id or author would make Liquibase re-run a migration that has
  already been applied. The historical migration record must stay as it is.
- **`scripts/rename_db_nyi.sql`**, the one-time database migration script, which
  has to name the retired database and role in order to rename them.

## No new document goes here

`docs/archive/` is append-only history. New documents are filed under the NYI
name in `docs/` or `specs/`, never here.