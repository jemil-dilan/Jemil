package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.application.inbound.usecase.RegisterAgencyUseCaseImpl.Command;
import cm.nyi.agency.domain.agency.AgencyId;

public interface RegisterAgencyUseCase {
    AgencyId execute(Command command);
}
