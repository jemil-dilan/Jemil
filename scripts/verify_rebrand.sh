#!/usr/bin/env bash
#
# verify_rebrand.sh — Sprint 0 (NYI_Sprint_Plan.md, step 8) acceptance gate.
#
# Fails if the retired project name appears anywhere in the working tree,
# except in the categories that cannot be renamed.
#
# The matching itself lives in scripts/verify_rebrand.py because the Liquibase
# <changeSet> allowlist has to span multiple lines (id= and author= are often on
# a different line from the tag), which a line-oriented regex cannot express
# correctly.
#
# Usage: scripts/verify_rebrand.sh [--verbose]
# Exit:  0 = clean, 1 = retired name found, 2 = bad invocation.

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if ! command -v python3 >/dev/null 2>&1; then
  echo "verify_rebrand.sh: python3 is required but was not found on PATH." >&2
  exit 2
fi

exec python3 "${SCRIPT_DIR}/verify_rebrand.py" "$@"