package cm.jemil.agency.domain.city;

import cm.jemil.agency.domain.city.views.CityView.CityView1;
import cm.jemil.shared.utils.PageData;
import cm.jemil.shared.utils.PaginationFetchRequest;

public interface CityRepository {
    void save(City city);

    boolean existsById(CityId id);

    PageData<CityView1> findAllView1(PaginationFetchRequest paginationFetchRequest);
}
