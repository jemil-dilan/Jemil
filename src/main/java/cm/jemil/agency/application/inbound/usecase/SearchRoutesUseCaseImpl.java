package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.exception.SameOriginAndDestinationException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchRoutesUseCaseImpl implements SearchRoutesUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public List<RouteSearchView> execute(UUID origin, UUID destination) {
        if (Objects.equals(origin, destination)) {
            throw new SameOriginAndDestinationException();
        }
        return agencyRepository.searchRoutes(new CityId(origin), new CityId(destination));
    }
}
