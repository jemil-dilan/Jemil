package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.branch.BranchId;

public interface DeleteBranchUseCase {
    void execute(BranchId branchId);
}
