package cm.nyi.agency.domain.city.views;

import cm.nyi.agency.domain.city.CityId;
import cm.nyi.agency.domain.city.CityName;
import cm.nyi.agency.domain.city.CityRegion;
import cm.nyi.shared.utils.CreatedAt;

public sealed interface CityView permits CityView.CityView1 {
    record CityView1(CityId id, CityName name, CityRegion region, CreatedAt createdAt) implements CityView {}
}
