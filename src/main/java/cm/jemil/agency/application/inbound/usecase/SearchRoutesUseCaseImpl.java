package cm.jemil.agency.application.inbound.usecase;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_012;

import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.shared.exception.DomainException;
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
            throw new DomainException(AGENCY_400_012);
        }
        return agencyRepository.searchRoutes(new CityId(origin), new CityId(destination));
    }
}
