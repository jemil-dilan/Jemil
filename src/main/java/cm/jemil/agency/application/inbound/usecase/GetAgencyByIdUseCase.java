package cm.jemil.agency.application.inbound.usecase;

import static cm.jemil.agency.domain.agency.views.AgencyView.*;

import java.util.UUID;

public interface GetAgencyByIdUseCase {
    AgencyView1 execute(UUID id);
}
