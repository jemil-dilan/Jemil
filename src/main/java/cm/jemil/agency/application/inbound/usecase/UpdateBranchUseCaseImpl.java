package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchAddress;
import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchName;
import cm.jemil.agency.domain.branch.BranchRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateBranchUseCaseImpl implements UpdateBranchUseCase {

    private final BranchRepository branchRepository;

    @Override
    public AgencyBranch execute(BranchId branchId, String name, String address) {
        var existingBranch = branchRepository.loadById(branchId);

        // Create a new branch with updated values, preserving the agency and city
        return AgencyBranch.of(
                existingBranch.agencyId(), new BranchName(name), new BranchAddress(address), existingBranch.cityId());
    }
}
