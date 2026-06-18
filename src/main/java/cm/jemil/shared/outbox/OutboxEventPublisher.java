package cm.jemil.shared.outbox;

public interface OutboxEventPublisher {
    void publish(Object domainEvent);
}
