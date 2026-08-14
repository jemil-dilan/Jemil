package cm.jemil.agency.domain.city;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.CITY_400_002;

import cm.jemil.shared.exception.DomainException;

/**
 * Value object representing the region of a city.
 * Enforces business rules: city region cannot be null or blank.
 */
public record CityRegion(String value) {
    public CityRegion {
        // Defense in depth: validate even though provides compile-time safety
        if (value == null) {
            throw new DomainException(CITY_400_002, "City region is required");
        }
        if (value.isBlank()) {
            throw new DomainException(CITY_400_002, "City region cannot be blank");
        }
    }
}
