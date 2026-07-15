package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAllBranchesByAgencyUseCaseImpl implements GetAllBranchesByAgencyUseCase {

    private final BranchRepository branchRepository;

    @Override
    public List<AgencyBranch> execute(AgencyId agencyId) {
        return branchRepository.findAllByAgencyId(agencyId);
    }
}
