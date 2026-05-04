package cm.jemil.agency.domain.port.in;

import cm.jemil.agency.domain.model.Agency;
import cm.jemil.agency.domain.model.AgencyId;
import java.util.List;
import java.util.Optional;

/** Port entrant : consultation des agences. */
public interface GetAgencyUseCase {

    Optional<Agency> findById(AgencyId agencyId);

    List<Agency> findAllActive();

    List<Agency> findByCity(String city);
}
