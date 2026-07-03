package cm.jemil.shared.events;

public record DomainEventType(String name, Class<? extends DomainEvent> eventClass) {
    public static DomainEventType from(Class<? extends DomainEvent> eventClass) {
        return new DomainEventType(eventClass.getSimpleName(), eventClass);
    }
}
