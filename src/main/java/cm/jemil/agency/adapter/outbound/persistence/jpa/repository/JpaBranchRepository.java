package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyJpaMapper;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchRepository;
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
}
