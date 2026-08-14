package cm.jemil.agency.domain.agency;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_003;

import cm.jemil.shared.exception.DomainException;

/**
 * Value object representing the name of an agency.
 * Enforces business rules: name cannot be null or blank.
 * Runtime validation ensures defense in depth for all code paths (REST, DB mappers, internal code).
 */
public record AgencyName(String value) {
    public AgencyName {
        // Defense in depth: validate null and blank
        // This handles cases where VOs are created from non-REST sources (e.g., DB mappers)
        if (value == null) {
            throw new DomainException(AGENCY_400_003, "Agency name is required");
        }
        if (value.isBlank()) {
            throw new DomainException(AGENCY_400_003, "Agency name cannot be blank");
        }
    }
}
