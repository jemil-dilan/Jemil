package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.agency.AgencyId;
import cm.nyi.agency.domain.agency.views.AgencyView;
import cm.nyi.agency.domain.branch.BranchRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAllBranchesByAgencyUseCaseImpl implements GetAllBranchesByAgencyUseCase {

    private final BranchRepository branchRepository;

    @Override
    public List<AgencyView.BranchView> execute(UUID agencyId) {
        return branchRepository.loadAllByAgencyId(new AgencyId(agencyId));
    }
}
