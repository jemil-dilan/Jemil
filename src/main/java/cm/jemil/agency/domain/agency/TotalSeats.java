package cm.jemil.agency.domain.agency;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_008;

import cm.jemil.shared.exception.DomainException;

/**
 * Value object representing the total number of seats on a route.
 * Enforces business rules: seat count must be positive.
 */
public record TotalSeats(int value) {
    public TotalSeats {
        if (value <= 0) {
            throw new DomainException(AGENCY_400_008, "Seat count must be positive");
        }
    }
}
