package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.branch.AgencyBranch;
import java.util.List;

public interface GetAllBranchesByAgencyUseCase {
    List<AgencyBranch> execute(AgencyId agencyId);
}
