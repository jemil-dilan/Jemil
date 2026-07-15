package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetBranchByIdUseCaseImpl implements GetBranchByIdUseCase {

    private final BranchRepository branchRepository;

    @Override
    public Optional<AgencyBranch> execute(BranchId branchId) {
        return branchRepository.findById(branchId);
    }
}
