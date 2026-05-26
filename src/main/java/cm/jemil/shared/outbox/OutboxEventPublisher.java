package cm.jemil.shared.outbox;

import cm.jemil.shared.events.DomainEvent;
import java.util.UUID;

public interface OutboxEventPublisher {
    void publish(DomainEvent domainEvent, UUID aggregateId);
}
