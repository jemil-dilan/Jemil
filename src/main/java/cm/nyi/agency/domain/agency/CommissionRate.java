package cm.nyi.agency.domain.agency;

import static cm.nyi.agency.domain.exception.AgencyErrorCode.AGENCY_400_010;

import cm.nyi.shared.exception.DomainException;
import java.math.BigDecimal;

/**
 * Value object representing the commission rate for an agency.
 * Ensures the rate is non-negative and within valid business bounds.
 */
public record CommissionRate(double value) {
    public CommissionRate {
        if (value < 0) {
            throw new DomainException(AGENCY_400_010, "Commission rate cannot be negative");
        }
        if (value > 100) {
            throw new DomainException(AGENCY_400_010, "Commission rate cannot exceed 100%");
        }
    }

    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(value);
    }
}
