package cm.jemil.agency.domain.city.views;

import java.util.UUID;

public sealed interface CityView permits CityView.CityView1 {
    record CityView1(UUID id, String name) implements CityView {}
}
