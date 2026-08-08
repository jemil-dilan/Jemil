package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchRoutesUseCaseImpl implements SearchRoutesUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public List<RouteSearchView> execute(String origin, String destination) {
        return agencyRepository.searchRoutes(origin, destination);
    }
}
