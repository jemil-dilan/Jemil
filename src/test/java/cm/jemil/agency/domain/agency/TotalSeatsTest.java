package cm.jemil.agency.domain.agency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

class TotalSeatsTest {

    @Test
    void shouldCreateTotalSeats() {
        var seats = new TotalSeats(50);
        assertThat(seats.value()).isEqualTo(50);
    }

    @Test
    void shouldRejectZeroSeats() {
        assertThatThrownBy(() -> new TotalSeats(0))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.AGENCY_400_008.getCode());
    }

    @Test
    void shouldRejectNegativeSeats() {
        assertThatThrownBy(() -> new TotalSeats(-5))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.AGENCY_400_008.getCode());
    }
}
