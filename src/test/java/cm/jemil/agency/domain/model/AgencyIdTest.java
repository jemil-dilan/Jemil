package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.AgencyId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgencyIdTest {

    @Test
    void shouldCreateFromUuid() {
        var uuid = UUID.randomUUID();
        var id = new AgencyId(uuid);

        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void shouldGenerateRandomId() {
        var id1 = AgencyId.generate();
        var id2 = AgencyId.generate();

        assertThat(id1).isNotNull();
        assertThat(id2).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void shouldBeEqualForSameValue() {
        var uuid = UUID.randomUUID();
        assertThat(new AgencyId(uuid)).isEqualTo(new AgencyId(uuid));
    }
}
