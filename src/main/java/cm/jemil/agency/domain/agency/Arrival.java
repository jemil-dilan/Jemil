package cm.jemil.agency.domain.agency;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_006;

import cm.jemil.shared.exception.DomainException;

/**
 * Value object representing the arrival city of a route.
 * Enforces business rules: arrival city cannot be null or blank.
 */
public record Arrival(String value) {
    public Arrival {
        // Defense in depth: validate null and blank
        if (value == null) {
            throw new DomainException(AGENCY_400_006, "Arrival city is required");
        }
        if (value.isBlank()) {
            throw new DomainException(AGENCY_400_006, "Arrival city cannot be blank");
        }
    }
}
