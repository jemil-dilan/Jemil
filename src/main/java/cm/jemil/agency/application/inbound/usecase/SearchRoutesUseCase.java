package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.views.RouteSearchView;
import java.util.List;

public interface SearchRoutesUseCase {
    List<RouteSearchView> execute(String origin, String destination);
}
