package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyJpaMapper;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

@RequiredArgsConstructor
public class JpaAgencyRepository implements AgencyRepository {

    private final AgencySpringRepository agencySpringRepository;
    private final AgencyJpaMapper mapper;

    @Override
    public void insert(Agency agency) {
        agencySpringRepository.save(mapper.toJpa(agency));
    }

    @Override
    public AgencyView1 loadByIdAgencyView1(AgencyId id) {
        return agencySpringRepository
                .findById(id.value())
                .map(mapper::toAgencyView1)
                .orElseThrow(() -> new DomainException(AgencyErrorCode.AGENCY_404_001));
    }

    @Override
    public Agency loadById(AgencyId id) {
        return agencySpringRepository
                .findById(id.value())
                .map(mapper::toDomain)
                .orElseThrow(() -> new DomainException(AgencyErrorCode.AGENCY_404_001));
    }

    @Override
    public List<AgencyView1> getAllAgencyView1() {
        return agencySpringRepository.findAll().stream()
                .map(mapper::toAgencyView1)
                .toList();
    }

    @Override
    public List<AgencyView1> getAllAgencyView1(String cityFilter) {
        return agencySpringRepository.findAllWithBranchesByCity(cityFilter).stream()
                .map(mapper::toAgencyView1)
                .toList();
    }

    @Override
    public Optional<Agency> findById(AgencyId id) {
        return agencySpringRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(AgencyId id) {
        return agencySpringRepository.existsById(id.value());
    }

    @Override
    public List<AgencyView1> getAllAgencyView1(String cityFilter, int page, int size) {
        return agencySpringRepository.findAllWithBranchesByCity(cityFilter, PageRequest.of(page, size)).stream()
                .map(mapper::toAgencyView1)
                .toList();
    }

    @Override
    public long countAgencies(String cityFilter) {
        if (cityFilter == null || cityFilter.isBlank()) {
            return agencySpringRepository.count();
        }
        return agencySpringRepository.countByBranchCityName(cityFilter);
    }
}
