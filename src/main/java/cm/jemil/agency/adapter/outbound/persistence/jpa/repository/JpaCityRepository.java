package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.CityJpaMapper;
import cm.jemil.agency.domain.city.City;
import cm.jemil.agency.domain.city.CityRepository;
import cm.jemil.agency.domain.city.views.CityView.CityView1;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaCityRepository implements CityRepository {

    private final CitySpringRepository citySpringRepository;
    private final CityJpaMapper mapper;

    @Override
    public void save(City city) {
        citySpringRepository.save(mapper.toJpa(city));
    }

    @Override
    public Optional<City> findById(UUID id) {
        return citySpringRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CityView1> findAllView1() {
        return citySpringRepository.findAll().stream().map(mapper::toCityView1).toList();
    }
}
