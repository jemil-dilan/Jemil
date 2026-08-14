package cm.jemil.agency.domain.agency;

import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.city.CityName;
import cm.jemil.shared.utils.PageData;
import cm.jemil.shared.utils.PaginationFetchRequest;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

public interface AgencyRepository {
    void insert(Agency agency);

    AgencyView1 loadByIdAgencyView1(AgencyId id);

    Agency loadById(AgencyId id);

    Optional<Agency> findById(AgencyId id);

    boolean existsById(AgencyId id);

    List<AgencyView1> loadAllAgency();

    List<AgencyView1> loadAllAgency(String cityFilter);

    PageData<AgencyView1> loadAllAgency(@Nullable CityName cityName, PaginationFetchRequest paginationFetchRequest);

    long countAgencies(String cityFilter);

    List<RouteSearchView> searchRoutes(CityId origin, CityId destination);

    void update(Agency agency);
}
