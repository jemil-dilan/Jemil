package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.branch.BranchId;

public interface DeleteBranchUseCase {
    void execute(BranchId branchId);
}
