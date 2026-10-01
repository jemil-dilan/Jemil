package cm.nyi.agency.application.inbound.usecase;

import static cm.nyi.agency.domain.agency.views.AgencyView.*;

import java.util.UUID;

public interface GetAgencyByIdUseCase {
    AgencyView1 execute(UUID id);
}
