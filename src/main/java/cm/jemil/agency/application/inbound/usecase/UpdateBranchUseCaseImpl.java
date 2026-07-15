package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchRepository;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateBranchUseCaseImpl implements UpdateBranchUseCase {

    private final BranchRepository branchRepository;

    @Override
    public AgencyBranch execute(BranchId branchId, String name, String address) {
        var existingBranch = branchRepository
                .findById(branchId)
                .orElseThrow(() -> new DomainException(AgencyErrorCode.BRANCH_404_001));

        // Create a new branch with updated values, preserving the city
        return AgencyBranch.of(name, address, existingBranch.getCityId());
    }
}
