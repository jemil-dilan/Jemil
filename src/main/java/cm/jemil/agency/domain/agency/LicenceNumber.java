package cm.jemil.agency.domain.agency;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_011;

import cm.jemil.shared.exception.DomainException;

/**
 * Value object representing the license number of an agency.
 * Enforces business rules: license number cannot be null or blank.
 */
public record LicenceNumber(String value) {
    public LicenceNumber {
        // Defense in depth: validate even though provides compile-time safety
        if (value == null) {
            throw new DomainException(AGENCY_400_011, "License number is required");
        }
        if (value.isBlank()) {
            throw new DomainException(AGENCY_400_011, "License number cannot be blank");
        }
    }
}
