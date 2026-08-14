package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.views.AgencyView;
import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchRepository;
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
