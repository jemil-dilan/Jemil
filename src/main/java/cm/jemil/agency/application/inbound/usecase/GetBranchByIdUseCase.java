package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchId;
import java.util.Optional;

public interface GetBranchByIdUseCase {
    Optional<AgencyBranch> execute(BranchId branchId);
}
