package cm.jemil.agency.domain.agency;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository {
    void save(Agency agency);

    Optional<Agency> findById(AgencyId id);

    List<Agency> findAll();
}
