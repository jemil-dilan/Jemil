package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.ScheduleId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ScheduleIdTest {

    @Test
    void shouldCreateFromUuid() {
        var uuid = UUID.randomUUID();
        assertThat(new ScheduleId(uuid).value()).isEqualTo(uuid);
    }

    @Test
    void shouldGenerateUniqueIds() {
        assertThat(ScheduleId.generate()).isNotEqualTo(ScheduleId.generate());
    }

    @Test
    void shouldBeEqualForSameValue() {
        var uuid = UUID.randomUUID();
        assertThat(new ScheduleId(uuid)).isEqualTo(new ScheduleId(uuid));
    }
}
