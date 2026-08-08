package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;

/**
 * Use case for suspending an agency.
 * Only ADMIN users can perform this action.
 */
public interface SuspendAgencyUseCase {
    AgencyView1 execute(AgencyId agencyId);
}
