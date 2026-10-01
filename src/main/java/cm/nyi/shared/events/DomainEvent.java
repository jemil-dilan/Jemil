package cm.nyi.shared.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    UUID eventId();

    Instant occurredAt();

    String aggregateType();

    String aggregateId();
}
