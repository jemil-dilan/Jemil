package cm.jemil.agency.domain.city;

import cm.jemil.agency.domain.city.views.CityView.CityView1;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityRepository {
    void save(City city);

    Optional<City> findById(UUID id);

    Optional<City> findByNameIgnoreCase(String name);

    List<CityView1> findAllView1();
}
