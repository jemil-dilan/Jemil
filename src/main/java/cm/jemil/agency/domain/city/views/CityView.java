package cm.jemil.agency.domain.city.views;

import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.city.CityName;
import cm.jemil.agency.domain.city.CityRegion;

public sealed interface CityView permits CityView.CityView1 {
    record CityView1(CityId id, CityName name, CityRegion region) implements CityView {}
}
