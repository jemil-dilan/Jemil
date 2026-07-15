package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchId;

public interface UpdateBranchUseCase {
    AgencyBranch execute(BranchId branchId, String name, String address);
}
