package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Arrival;
import cm.jemil.agency.domain.agency.Departure;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import java.util.List;

public interface SearchRoutesUseCase {
    List<RouteSearchView> execute(Departure origin, Arrival destination);
}
