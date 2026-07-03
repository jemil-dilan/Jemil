package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.branch.AgencyBranch;
import java.util.UUID;

public interface AddBranchUseCase {
    AgencyBranch execute(UUID agencyId, String name, String address, UUID cityId);
}
