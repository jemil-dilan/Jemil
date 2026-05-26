# ADR-002: Choice of Database Migration Tool — Liquibase

## Status
Accepted

## Context
We need a reliable way to manage database schema changes across different environments and as the modular monolith evolves.

## Decision
We choose **Liquibase** over Flyway.

### Reasons:
- **XML/YAML/JSON formats**: Allows for structured, platform-independent change definitions.
- **Preconditions**: Can check the state of the database before applying a change.
- **Rollback support**: Native support for rolling back changes.
- **Contexts & Labels**: Easy management of test data vs. production schema.

## Consequences
- Requires a bit more learning curve than plain SQL-based migrations (Flyway).
- Ensures high consistency and safety during deployments.
