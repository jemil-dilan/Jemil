package cm.jemil.agency.domain.agency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

class AgencyNameTest {

    @Test
    void shouldCreateAgencyName() {
        var name = new AgencyName("Global Voyages");
        assertThat(name.value()).isEqualTo("Global Voyages");
    }

    @Test
    void shouldRejectNullName() {
        assertThatThrownBy(() -> new AgencyName(null))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.AGENCY_400_003.getCode());
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> new AgencyName("   "))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.AGENCY_400_003.getCode());
    }
}
