package cm.nyi.shared.outbox;

public interface OutboxEventPublisher {
    void publish(Object domainEvent);
}
