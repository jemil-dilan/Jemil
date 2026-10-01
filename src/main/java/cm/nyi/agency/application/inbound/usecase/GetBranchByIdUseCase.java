package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.agency.views.AgencyView;
import java.util.UUID;

public interface GetBranchByIdUseCase {
    AgencyView.BranchView execute(UUID branchId);
}
