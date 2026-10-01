package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.branch.BranchId;
import cm.nyi.agency.domain.branch.BranchRepository;
import cm.nyi.agency.domain.exception.BranchNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteBranchUseCaseImpl implements DeleteBranchUseCase {

    private final BranchRepository branchRepository;

    @Override
    public void execute(BranchId branchId) {
        if (!branchRepository.existsById(branchId)) {
            throw new BranchNotFoundException();
        }
        branchRepository.delete(branchId);
    }
}
