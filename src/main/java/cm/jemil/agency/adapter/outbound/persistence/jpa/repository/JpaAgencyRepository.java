package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyJpaMapper;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.city.CityName;
import cm.jemil.agency.domain.exception.AgencyNotFoundException;
import cm.jemil.shared.utils.PageData;
import cm.jemil.shared.utils.PaginationFetchRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class JpaAgencyRepository implements AgencyRepository {

    private final AgencySpringRepository agencySpringRepository;
    private final AgencyJpaMapper jpaMapper;

    @Override
    public void insert(Agency agency) {
        agencySpringRepository.saveAndFlush(jpaMapper.toJpa(agency));
    }

    @Override
    public AgencyView1 loadByIdAgencyView1(AgencyId id) {
        return agencySpringRepository
                .findById(id.value())
                .map(jpaMapper::toAgencyView1)
                .orElseThrow(AgencyNotFoundException::new);
    }

    @Override
    public Agency loadById(AgencyId id) {
        return agencySpringRepository
                .findById(id.value())
                .map(jpaMapper::toDomain)
                .orElseThrow(AgencyNotFoundException::new);
    }

    @Override
    public List<AgencyView1> loadAllAgency() {
        return agencySpringRepository.findAllWithBranches().stream()
                .map(jpaMapper::toAgencyView1)
                .toList();
    }

    @Override
    public List<AgencyView1> loadAllAgency(String cityFilter) {
        if (cityFilter == null || cityFilter.isBlank()) {
            return loadAllAgency();
        }
        return agencySpringRepository.findAllWithBranchesByCity(cityFilter).stream()
                .map(jpaMapper::toAgencyView1)
                .toList();
    }

    @Override
    public Optional<Agency> findById(AgencyId id) {
        return agencySpringRepository.findById(id.value()).map(jpaMapper::toDomain);
    }

    @Override
    public boolean existsById(AgencyId id) {
        return agencySpringRepository.existsById(id.value());
    }

    @Override
    public PageData<AgencyView1> loadAllAgency(
            @Nullable CityName cityName, PaginationFetchRequest paginationFetchRequest) {
        var city = Optional.ofNullable(cityName).map(CityName::value).orElse(null);
        Pageable pageable = PageRequest.of(paginationFetchRequest.page(), paginationFetchRequest.limit());
        var allAgencies = agencySpringRepository.findAllAgencies(city, AgencyStatus.ACTIVE, pageable);

        var views =
                allAgencies.getContent().stream().map(jpaMapper::toAgencyView1).toList();
        return new PageData<>(
                allAgencies.getTotalElements(),
                views,
                allAgencies.getTotalPages(),
                allAgencies.getSize(),
                allAgencies.getNumber());
    }

    @Override
    public long countAgencies(String cityFilter) {
        if (cityFilter == null || cityFilter.isBlank()) {
            return agencySpringRepository.count();
        }
        return agencySpringRepository.countByBranchCityName(cityFilter);
    }

    @Override
    public List<RouteSearchView> searchRoutes(CityId origin, CityId destination) {
        return agencySpringRepository.findRoutesByCities(origin.value(), destination.value()).stream()
                .map(jpaMapper::toRouteSearchView)
                .toList();
    }

    @Override
    public void update(@NonNull Agency agency) {
        agencySpringRepository.findById(agency.id()).ifPresent(agencyJpa -> {
            jpaMapper.fromAgencyDomain(agencyJpa, agency);
            agencySpringRepository.save(agencyJpa);
        });
    }
}
