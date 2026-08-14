package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyJpaMapper;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.views.AgencyView.BranchView;
import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchRepository;
import cm.jemil.agency.domain.exception.BranchNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaBranchRepository implements BranchRepository {

    private final AgencyBranchSpringRepository branchSpringRepository;
    private final AgencyJpaMapper jpaMapper;

    @Override
    public void save(AgencyBranch branch) {
        branchSpringRepository.save(jpaMapper.toJpa(branch));
    }

    @Override
    public BranchView loadById(BranchId branchId) {
        return branchSpringRepository
                .findById(branchId.value())
                .map(jpaMapper::toView)
                .orElseThrow(BranchNotFoundException::new);
    }

    @Override
    public List<BranchView> loadAllByAgencyId(AgencyId agencyId) {
        return branchSpringRepository.findAllByAgencyIdWithCity(agencyId.value()).stream()
                .map(jpaMapper::toView)
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
