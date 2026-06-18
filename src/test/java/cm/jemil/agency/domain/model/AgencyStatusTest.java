package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.AgencyStatus;
import org.junit.jupiter.api.Test;

class AgencyStatusTest {

    @Test
    void shouldHaveExpectedValues() {
        assertThat(AgencyStatus.values())
                .containsExactly(AgencyStatus.ACTIVE, AgencyStatus.SUSPENDED, AgencyStatus.INACTIVE);
    }

    @Test
    void shouldParseFromString() {
        assertThat(AgencyStatus.valueOf("ACTIVE")).isEqualTo(AgencyStatus.ACTIVE);
        assertThat(AgencyStatus.valueOf("SUSPENDED")).isEqualTo(AgencyStatus.SUSPENDED);
        assertThat(AgencyStatus.valueOf("INACTIVE")).isEqualTo(AgencyStatus.INACTIVE);
    }
}
