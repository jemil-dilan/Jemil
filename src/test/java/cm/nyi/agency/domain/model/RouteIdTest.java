package cm.nyi.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import cm.nyi.agency.domain.agency.RouteId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RouteIdTest {

    @Test
    void shouldCreateFromUuid() {
        var uuid = UUID.randomUUID();
        assertThat(new RouteId(uuid).value()).isEqualTo(uuid);
    }

    @Test
    void shouldGenerateUniqueIds() {
        assertThat(RouteId.generate()).isNotEqualTo(RouteId.generate());
    }

    @Test
    void shouldBeEqualForSameValue() {
        var uuid = UUID.randomUUID();
        assertThat(new RouteId(uuid)).isEqualTo(new RouteId(uuid));
    }
}
