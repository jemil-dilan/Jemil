package cm.jemil.shared.outbox;

import cm.jemil.shared.events.DomainEvent;

public interface OutboxEventPublisher {
    void publish(DomainEvent domainEvent);
}
