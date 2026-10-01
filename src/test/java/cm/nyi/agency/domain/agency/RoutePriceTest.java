package cm.nyi.agency.domain.agency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.nyi.agency.domain.exception.AgencyErrorCode;
import cm.nyi.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

class RoutePriceTest {

    @Test
    void shouldCreateRoutePrice() {
        var price = new RoutePrice(java.math.BigDecimal.valueOf(5000));
        assertThat(price.price()).isEqualByComparingTo(java.math.BigDecimal.valueOf(5000));
    }

    @Test
    void shouldAllowZeroPrice() {
        var price = new RoutePrice(java.math.BigDecimal.ZERO);
        assertThat(price.price()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
    }

    @Test
    void shouldRejectNullPrice() {
        assertThatThrownBy(() -> new RoutePrice(null))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.AGENCY_400_007.getCode());
    }

    @Test
    void shouldRejectNegativePrice() {
        assertThatThrownBy(() -> new RoutePrice(java.math.BigDecimal.valueOf(-1)))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.AGENCY_400_007.getCode());
    }

    @Test
    void shouldExposeWholeXafAmount() {
        var price = RoutePrice.ofXaf(5000);

        assertThat(price.amountXaf()).isEqualTo(5000);
    }

    @Test
    void shouldRejectFractionalXafAmount() {
        assertThatThrownBy(() -> new RoutePrice(java.math.BigDecimal.valueOf(5000.50)))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.AGENCY_400_007.getCode());
    }
}
