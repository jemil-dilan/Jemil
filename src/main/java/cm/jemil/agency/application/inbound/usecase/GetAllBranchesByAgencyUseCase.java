package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.views.AgencyView.BranchView;
import java.util.List;
import java.util.UUID;

public interface GetAllBranchesByAgencyUseCase {
    List<BranchView> execute(UUID agencyId);
}
