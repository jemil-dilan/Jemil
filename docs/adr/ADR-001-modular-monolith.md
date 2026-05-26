# ADR-001: Architectural Choice — Modular Monolith

## Status
Accepted

## Context
The JEMIL project needs to balance rapid development with future scalability. While microservices offer the highest scalability, they introduce significant operational complexity (distributed transactions, network latency, complex deployment).

## Decision
We choose a **Modular Monolith** architecture for the MVP phase.

### Key Characteristics:
- Single deployment unit (one JAR).
- Strict logical separation of business domains (agency, booking, payment, etc.).
- Communication between modules via domain events (Outbox pattern).
- Dependency boundaries enforced by ArchUnit.

## Consequences
- **Pros**: Simplified development and deployment, easier debugging, no network overhead between modules.
- **Cons**: Still a single point of failure at the infrastructure level.
- **Future**: The strict boundaries allow extracting any module into a separate microservice with minimal effort if needed.
