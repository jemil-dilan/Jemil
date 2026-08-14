package cm.jemil.agency.domain.agency;

import static org.junit.jupiter.api.Assertions.*;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

class CommissionRateTest {

    @Test
    void shouldCreateCommissionRateWithValidValue() {
        CommissionRate rate = new CommissionRate(10.0);
        assertEquals(10.0, rate.value());
    }

    @Test
    void shouldCreateCommissionRateWithZero() {
        CommissionRate rate = new CommissionRate(0.0);
        assertEquals(0.0, rate.value());
    }

    @Test
    void shouldCreateCommissionRateWithMaxValue() {
        CommissionRate rate = new CommissionRate(100.0);
        assertEquals(100.0, rate.value());
    }

    @Test
    void shouldRejectNegativeCommissionRate() {
        DomainException exception = assertThrows(DomainException.class, () -> new CommissionRate(-1.0));
        assertEquals(AgencyErrorCode.AGENCY_400_010.getCode(), exception.getCode());
        assertEquals("Commission rate cannot be negative", exception.getMessage());
    }

    @Test
    void shouldRejectCommissionRateExceeding100() {
        DomainException exception = assertThrows(DomainException.class, () -> new CommissionRate(101.0));
        assertEquals(AgencyErrorCode.AGENCY_400_010.getCode(), exception.getCode());
        assertEquals("Commission rate cannot exceed 100%", exception.getMessage());
    }

    @Test
    void shouldConvertToBigDecimal() {
        CommissionRate rate = new CommissionRate(15.5);
        assertEquals(15.5, rate.toBigDecimal().doubleValue());
    }
}
