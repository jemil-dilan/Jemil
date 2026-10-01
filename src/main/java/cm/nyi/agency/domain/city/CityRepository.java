package cm.nyi.agency.domain.city;

import cm.nyi.agency.domain.city.views.CityView.CityView1;
import cm.nyi.shared.utils.PageData;
import cm.nyi.shared.utils.PaginationFetchRequest;

public interface CityRepository {
    void save(City city);

    boolean existsById(CityId id);

    PageData<CityView1> findAllView1(PaginationFetchRequest paginationFetchRequest);
}
