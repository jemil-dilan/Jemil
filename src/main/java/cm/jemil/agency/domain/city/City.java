package cm.jemil.agency.domain.city;

import cm.jemil.shared.utils.CreatedAt;
import lombok.Getter;

@Getter
public class City {
    private final CityId id;
    private CityName name;
    private CityRegion region;
    private CreatedAt createdAt;

    public City(CityId id, CityName name, CityRegion region, CreatedAt createdAt) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.createdAt = createdAt;
    }

    public static City of(CityName name, CityRegion region) {
        return new City(CityId.generate(), name, region, CreatedAt.now());
    }
}
