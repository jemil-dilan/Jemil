package cm.jemil.agency.domain.city;

import java.util.UUID;

public record CityId(UUID value) {
    public static CityId generate() {
        return new CityId(UUID.randomUUID());
    }
}
