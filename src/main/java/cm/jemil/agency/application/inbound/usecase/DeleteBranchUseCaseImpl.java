package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchRepository;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteBranchUseCaseImpl implements DeleteBranchUseCase {

    private final BranchRepository branchRepository;

    @Override
    public void execute(BranchId branchId) {
        if (!branchRepository.existsById(branchId)) {
            throw new DomainException(AgencyErrorCode.BRANCH_404_001);
        }
        branchRepository.delete(branchId);
    }
}
