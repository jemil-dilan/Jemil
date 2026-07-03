package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCaseImpl.Command;
import cm.jemil.agency.domain.agency.AgencyId;

public interface RegisterAgencyUseCase {
    AgencyId execute(Command command);
}
