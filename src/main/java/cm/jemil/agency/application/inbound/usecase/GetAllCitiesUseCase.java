package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.city.views.CityView.CityView1;
import java.util.List;

public interface GetAllCitiesUseCase {
    List<CityView1> execute();
}
