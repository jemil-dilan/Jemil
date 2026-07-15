package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyJpaMapper;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaBranchRepository implements BranchRepository {

    private final AgencyBranchSpringRepository branchSpringRepository;
    private final AgencySpringRepository agencySpringRepository;
    private final CitySpringRepository citySpringRepository;
    private final AgencyJpaMapper mapper;

    @Override
    public void save(AgencyBranch branch, AgencyId agencyId) {
        var agencyJpa = agencySpringRepository.getReferenceById(agencyId.value());
        var cityJpa = citySpringRepository.getReferenceById(branch.getCityId().value());
        var branchJpa = mapper.toJpa(branch);
        branchJpa.setAgency(agencyJpa);
        branchJpa.setCity(cityJpa);
        branchSpringRepository.save(branchJpa);
    }

    @Override
    public Optional<AgencyBranch> findById(BranchId branchId) {
        return branchSpringRepository.findById(branchId.value()).map(mapper::toDomain);
    }

    @Override
    public List<AgencyBranch> findAllByAgencyId(AgencyId agencyId) {
        return branchSpringRepository.findAllByAgencyIdWithCity(agencyId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(BranchId branchId) {
        branchSpringRepository.deleteById(branchId.value());
    }

    @Override
    public boolean existsById(BranchId branchId) {
        return branchSpringRepository.existsById(branchId.value());
    }
}
