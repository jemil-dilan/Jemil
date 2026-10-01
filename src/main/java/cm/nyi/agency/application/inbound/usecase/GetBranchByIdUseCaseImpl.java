package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.agency.views.AgencyView;
import cm.nyi.agency.domain.branch.BranchId;
import cm.nyi.agency.domain.branch.BranchRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetBranchByIdUseCaseImpl implements GetBranchByIdUseCase {

    private final BranchRepository branchRepository;

    @Override
    public AgencyView.BranchView execute(UUID branchId) {
        return branchRepository.loadById(new BranchId(branchId));
    }
}
