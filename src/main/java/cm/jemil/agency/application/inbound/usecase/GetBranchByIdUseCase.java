package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.views.AgencyView;
import java.util.UUID;

public interface GetBranchByIdUseCase {
    AgencyView.BranchView execute(UUID branchId);
}
