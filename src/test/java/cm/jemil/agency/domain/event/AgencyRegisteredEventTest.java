package cm.jemil.agency.domain.event;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyRegisteredEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgencyRegisteredEventTest {

    @Test
    void shouldCreateEventFromAgency() {
        var agencyId = AgencyId.generate();

        var event = AgencyRegisteredEvent.of(agencyId, new AgencyName("Global Voyages"));

        assertThat(event.eventId()).isNotNull();
        assertThat(event.agencyId()).isEqualTo(agencyId);
        assertThat(event.name()).isEqualTo(new AgencyName("Global Voyages"));
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    void shouldImplementDomainEvent() {
        var event = AgencyRegisteredEvent.of(AgencyId.generate(), new AgencyName("Test"));

        assertThat(event.eventId()).isInstanceOf(UUID.class);
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    void shouldGenerateUniqueEventIds() {
        var event1 = AgencyRegisteredEvent.of(AgencyId.generate(), new AgencyName("A"));
        var event2 = AgencyRegisteredEvent.of(AgencyId.generate(), new AgencyName("B"));

        assertThat(event1.eventId()).isNotEqualTo(event2.eventId());
    }
}
