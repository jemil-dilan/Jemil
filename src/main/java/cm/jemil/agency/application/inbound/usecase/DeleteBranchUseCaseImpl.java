package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchRepository;
import cm.jemil.agency.domain.exception.BranchNotFoundException;
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
