package cm.jemil.agency.domain.agency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

class DepartureArrivalTest {

    @Test
    void shouldCreateDepartureAndArrival() {
        var departure = new Departure("Douala");
        var arrival = new Arrival("Yaoundé");
        assertThat(departure.value()).isEqualTo("Douala");
        assertThat(arrival.value()).isEqualTo("Yaoundé");
    }

    @Test
    void shouldRejectBlankDeparture() {
        assertThatThrownBy(() -> new Departure(" "))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.AGENCY_400_006.getCode());
    }

    @Test
    void shouldRejectBlankArrival() {
        assertThatThrownBy(() -> new Arrival(" "))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.AGENCY_400_006.getCode());
    }
}
