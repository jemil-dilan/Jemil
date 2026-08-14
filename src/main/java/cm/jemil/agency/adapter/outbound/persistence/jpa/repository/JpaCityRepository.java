package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.CityJpaMapper;
import cm.jemil.agency.domain.city.City;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.city.CityRepository;
import cm.jemil.agency.domain.city.views.CityView.CityView1;
import cm.jemil.shared.utils.PageData;
import cm.jemil.shared.utils.PaginationFetchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class JpaCityRepository implements CityRepository {

    private final CitySpringRepository citySpringRepository;
    private final CityJpaMapper mapper;

    @Override
    public void save(City city) {
        citySpringRepository.save(mapper.toJpa(city));
    }

    @Override
    public boolean existsById(CityId id) {
        return citySpringRepository.existsById(id.value());
    }

    @Override
    public PageData<CityView1> findAllView1(PaginationFetchRequest paginationFetchRequest) {
        Pageable pageable = PageRequest.of(paginationFetchRequest.page(), paginationFetchRequest.limit());
        var allCities = citySpringRepository.findAllCities(pageable);
        return new PageData<>(
                allCities.getTotalElements(),
                allCities.getContent(),
                allCities.getTotalPages(),
                allCities.getSize(),
                allCities.getNumber());
    }
}
