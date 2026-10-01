package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.branch.AgencyBranch;
import cm.nyi.agency.domain.branch.BranchId;

public interface UpdateBranchUseCase {
    AgencyBranch execute(BranchId branchId, String name, String address);
}
