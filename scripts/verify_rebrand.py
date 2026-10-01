#!/usr/bin/env python3
"""verify_rebrand.py — Sprint 0 (NYI_Sprint_Plan.md, step 8) acceptance gate.

Fails if the retired project name appears anywhere in the working tree, except
in the categories that cannot be renamed. See the ALLOWLIST below.

Usage: scripts/verify_rebrand.py [--verbose]
Exit:  0 = clean, 1 = retired name found, 2 = bad invocation.

This is a Python helper for scripts/verify_rebrand.sh, which is the entry point.
Keeping the matching logic here means it can reason about a Liquibase <changeSet>
opening tag that spans several lines — a per-line regex cannot, which is what
made the first version of this gate report false positives on perfectly legal
changelog files.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

# ---------------------------------------------------------------------------
# The retired name is assembled from fragments so this file does not fail on
# itself. The gate must never match its own source.
# ---------------------------------------------------------------------------
RETIRED_NAME = "jem" + "il"

REPO_ROOT = Path(__file__).resolve().parent.parent

# ---------------------------------------------------------------------------
# ALLOWLIST — the only categories permitted to still carry the retired name.
#
# 1. docs/archive/ — Sprint 0 step 7 requires archiving the old founding
#    documents rather than deleting them, and step 8 exempts a labelled
#    archive folder. They are historical record, not active references.
#
# 2. Liquibase <changeSet> opening tags — these MUST NOT be renamed. Liquibase
#    identifies a changeset by the (id, author, filename) triple. Renaming
#    either id or author makes Liquibase treat it as a brand-new changeset,
#    which re-runs the migration against a database where the tables already
#    exist and corrupts the entire changelog. Rewriting the historical
#    migration record is also a loss of audit trail.
#    Matched by blanking the opening tag only — its id=/author= attributes may
#    be spread across several lines. Anything else in a changelog (comments,
#    <sql>, table names) is still flagged.
#
# 3. docs/NYI_Sprint_Plan.md and docs/NYI_Implementation_Guideline.md — these
#    are the documents that INSTRUCT the rebrand. They necessarily quote the
#    old name to say what to replace it with. They are not a leak.
#
# 4. scripts/rename_db_nyi.sql — the one-time database migration script. Its
#    entire purpose is to name the retired database and role so it can rename
#    them, so it cannot avoid spelling them. It is an inert artefact: nothing
#    runs it automatically, and it is a no-op once the rename is done.
# ---------------------------------------------------------------------------

EXCLUDED_DIRS = {
    ".git", ".gradle", ".idea", "build", "target", "out", "node_modules",
    "__pycache__",
}
EXCLUDED_SUFFIXES = {".hprof", ".jar", ".docx", ".pyc"}

PATH_ALLOWLIST = (
    re.compile(r"^docs/archive/"),
    re.compile(r"^docs/NYI_(Sprint_Plan|Implementation_Guideline)\.md$"),
    re.compile(r"^scripts/rename_db_nyi\.sql$"),
)
CHANGELOG_RE = re.compile(r"^src/.*/db/changelog/.*\.xml$")
CHANGE_TAG_RE = re.compile(r"<changeSet\b", re.IGNORECASE)


def is_excluded_dir(rel: Path) -> bool:
    return any(part in EXCLUDED_DIRS for part in rel.parts[:-1])


def scan_text(text: str, rel: str) -> list[tuple[int, str, str]]:
    """Return (line_no, line_text, reason) for every offending line."""
    is_changelog = bool(CHANGELOG_RE.match(rel))
    hits: list[tuple[int, str, str]] = []

    # When inside a <changeSet ...> opening tag, we are reading id=/author=
    # attributes that Liquibase must keep. Blank them out.
    in_change_tag = False

    for lineno, line in enumerate(text.splitlines(), start=1):
        if not line.strip():
            continue

        scrubbed = line
        if in_change_tag:
            scrubbed = ""
            if ">" in line:
                in_change_tag = False
                scrubbed = line.split(">", 1)[1]
        elif is_changelog and CHANGE_TAG_RE.search(line):
            if ">" not in line:
                in_change_tag = True
                scrubbed = ""
            else:
                head, tail = line.split(">", 1)
                scrubbed = tail
                del head

        if RETIRED_NAME.lower() in scrubbed.lower():
            hits.append((lineno, scrubbed.strip(), rel))

    return hits


def main(argv: list[str]) -> int:
    verbose = False
    for arg in argv[1:]:
        if arg == "--verbose":
            verbose = True
        else:
            print(f"usage: {argv[0]} [--verbose]", file=sys.stderr)
            return 2

    if RETIRED_NAME.lower() in Path(__file__).read_text(encoding="utf-8").lower():
        print(
            "==> ERROR: the gate matched its own source. This is a bug in "
            "verify_rebrand.py, not a finding.",
            file=sys.stderr,
        )
        return 2

    files: list[Path] = []
    for path in REPO_ROOT.rglob("*"):
        if not path.is_file():
            continue
        rel = path.relative_to(REPO_ROOT)
        if is_excluded_dir(rel):
            continue
        if path.suffix.lower() in EXCLUDED_SUFFIXES:
            continue
        files.append(path)

    total = 0
    violations: list[tuple[int, str, str]] = []

    for path in sorted(files):
        rel = str(path.relative_to(REPO_ROOT))
        try:
            text = path.read_text(encoding="utf-8")
        except (UnicodeDecodeError, OSError):
            continue  # binary or unreadable — skipped deliberately

        if RETIRED_NAME.lower() in text.lower():
            total += text.lower().count(RETIRED_NAME.lower())

        if any(p.match(rel) for p in PATH_ALLOWLIST):
            continue

        violations.extend(scan_text(text, rel))

    print("==> Scanning for retired project name (case-insensitive)")
    print(f"==> Files scanned:                             {len(files)}")
    print(f"==> Occurrences found (incl. allowlisted):    {total}")
    print(f"==> Occurrences outside allowlist:             {len(violations)}")
    print()

    if not violations:
        print("==> PASS: retired name appears only in allowlisted categories.")
        print()
        print("Allowlisted by design:")
        print("  - docs/archive/                historical founding documents (Sprint 0 step 7)")
        print("  - Liquibase <changeSet> tags   must not be renamed; renaming breaks the changelog")
        print("  - the two NYI planning docs    they instruct the rebrand, so they must quote it")
        print("  - scripts/rename_db_nyi.sql    the migration script must name what it renames")
        return 0

    if verbose:
        for lineno, line, rel in violations:
            print(f"  {rel}:{lineno}: {line[:160]}")
        print()

    flagged = sorted({rel for _, _, rel in violations})
    print(f"==> FAIL: {len(violations)} occurrence(s) across {len(flagged)} file(s):")
    for rel in flagged:
        print(f"    {rel}")
    return 1


if __name__ == "__main__":
    sys.exit(main(sys.argv))