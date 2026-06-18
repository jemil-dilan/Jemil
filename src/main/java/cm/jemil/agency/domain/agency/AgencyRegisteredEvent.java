package cm.jemil.agency.domain.agency;

import cm.jemil.shared.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record AgencyRegisteredEvent(UUID eventId, AgencyId agencyId, String name, Instant occurredAt)
        implements DomainEvent {
    public static AgencyRegisteredEvent of(AgencyId agencyId, String name) {
        return new AgencyRegisteredEvent(UUID.randomUUID(), agencyId, name, Instant.now());
    }
}
