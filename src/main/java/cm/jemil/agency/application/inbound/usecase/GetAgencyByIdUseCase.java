package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import java.util.Optional;

public interface GetAgencyByIdUseCase {
    Optional<Agency> execute(AgencyId id);
}
