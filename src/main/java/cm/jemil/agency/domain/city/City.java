package cm.jemil.agency.domain.city;

import lombok.Getter;

@Getter
public class City {
    private final CityId id;
    private CityName name;
    private CityRegion region;

    public City(CityId id, CityName name, CityRegion region) {
        this.id = id;
        this.name = name;
        this.region = region;
    }

    public static City of(CityName name, CityRegion region) {
        return new City(CityId.generate(), name, region);
    }
}
