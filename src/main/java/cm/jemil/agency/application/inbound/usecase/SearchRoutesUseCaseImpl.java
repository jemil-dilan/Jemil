package cm.jemil.agency.application.inbound.usecase;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_006;
import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_012;

import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.Arrival;
import cm.jemil.agency.domain.agency.Departure;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.shared.exception.DomainException;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchRoutesUseCaseImpl implements SearchRoutesUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public List<RouteSearchView> execute(Departure origin, Arrival destination) {
        if (origin.value().equalsIgnoreCase(destination.value())) {
            throw new DomainException(AGENCY_400_012);
        }
        return agencyRepository.searchRoutes(origin, destination);
    }
}
