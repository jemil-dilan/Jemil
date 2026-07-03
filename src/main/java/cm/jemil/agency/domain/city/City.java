package cm.jemil.agency.domain.city;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class City {
    private final CityId id;
    private String name;

    public static City of(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException(AgencyErrorCode.CITY_400_001);
        }
        return new City(CityId.generate(), name);
    }
}
