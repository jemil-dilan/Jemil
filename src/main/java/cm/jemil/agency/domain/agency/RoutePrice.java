package cm.jemil.agency.domain.agency;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_007;

import cm.jemil.shared.exception.DomainException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value object representing the price of a route.
 * Enforces business rules: price must be non-null and positive.
 */
public record RoutePrice(BigDecimal price) {
    public RoutePrice {
        // Defense in depth: validate even though provides compile-time safety
        if (price == null) {
            throw new DomainException(AGENCY_400_007, "Route price is required");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(AGENCY_400_007, "Route price must be positive");
        }
        if (price.stripTrailingZeros().scale() > 0) {
            throw new DomainException(AGENCY_400_007, "Route price must be a whole XAF amount");
        }
    }

    public static RoutePrice ofXaf(int amountXaf) {
        return new RoutePrice(BigDecimal.valueOf(amountXaf));
    }

    public int amountXaf() {
        return price.setScale(0, RoundingMode.UNNECESSARY).intValueExact();
    }
}
