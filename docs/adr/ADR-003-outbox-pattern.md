# ADR-003: Choice of Inter-Module Communication — Outbox Pattern

## Status
Accepted

## Context
Modules in a modular monolith should be decoupled. Direct service calls between modules (e.g., `BookingService` calling `PaymentService`) create tight coupling and make future extraction into microservices difficult.

## Decision
We choose the **Transactional Outbox Pattern** for asynchronous inter-module communication.

### Implementation:
1. When a business action occurs, the domain event is saved to an `outbox_events` table in the same database transaction.
2. An `OutboxScheduler` polls the table and publishes events via Spring's `ApplicationEventPublisher`.
3. Other modules listen to these events.

## Consequences
- **Pros**: Guaranteed at-least-once delivery, strong decoupling, resilient to failure of the consumer module.
- **Cons**: Slightly increased complexity and storage overhead.
