package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.views.RouteSearchView;
import java.util.List;
import java.util.UUID;

public interface SearchRoutesUseCase {
    List<RouteSearchView> execute(UUID origin, UUID destination);
}
