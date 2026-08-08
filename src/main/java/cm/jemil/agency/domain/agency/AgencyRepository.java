package cm.jemil.agency.domain.agency;

import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import java.util.List;
import java.util.Optional;

public interface AgencyRepository {
    void insert(Agency agency);

    AgencyView1 loadByIdAgencyView1(AgencyId id);

    Agency loadById(AgencyId id);

    Optional<Agency> findById(AgencyId id);

    boolean existsById(AgencyId id);

    List<AgencyView1> getAllAgencyView1();

    List<AgencyView1> getAllAgencyView1(String cityFilter);

    List<AgencyView1> getAllAgencyView1(String cityFilter, int page, int size);

    long countAgencies(String cityFilter);

    List<RouteSearchView> searchRoutes(Departure origin, Arrival destination);

    void save(Agency agency);
}
