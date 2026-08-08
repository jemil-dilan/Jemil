package cm.jemil.agency.domain.agency;

import cm.jemil.shared.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record AgencyRegisteredEvent(UUID eventId, AgencyId agencyId, AgencyName name, Instant occurredAt)
        implements DomainEvent {
    public static AgencyRegisteredEvent of(AgencyId agencyId, AgencyName name) {
        return new AgencyRegisteredEvent(UUID.randomUUID(), agencyId, name, Instant.now());
    }

    @Override
    public String aggregateType() {
        return "Agency";
    }

    @Override
    public String aggregateId() {
        return agencyId.value().toString();
    }
}
