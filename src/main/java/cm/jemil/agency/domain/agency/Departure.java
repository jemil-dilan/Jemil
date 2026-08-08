package cm.jemil.agency.domain.agency;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_006;

import cm.jemil.shared.exception.DomainException;

/**
 * Value object representing the departure city of a route.
 * Enforces business rules: departure city cannot be null or blank.
 */
public record Departure(String value) {
    public Departure {
        // Defense in depth: validate null and blank
        if (value == null) {
            throw new DomainException(AGENCY_400_006, "Departure city is required");
        }
        if (value.isBlank()) {
            throw new DomainException(AGENCY_400_006, "Departure city cannot be blank");
        }
    }
}



