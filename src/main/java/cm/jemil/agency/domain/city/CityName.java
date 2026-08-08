package cm.jemil.agency.domain.city;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.CITY_400_001;

import cm.jemil.shared.exception.DomainException;


/**
 * Value object representing the name of a city.
 * Enforces business rules: city name cannot be null or blank.
 */
public record CityName(String value) {
    public CityName {
        // Defense in depth: validate even though provides compile-time safety
        if (value == null) {
            throw new DomainException(CITY_400_001, "City name is required");
        }
        if (value.isBlank()) {
            throw new DomainException(CITY_400_001, "City name cannot be blank");
        }
    }
}


