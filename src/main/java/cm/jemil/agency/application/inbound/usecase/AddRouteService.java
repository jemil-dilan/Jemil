package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.Arrival;
import cm.jemil.agency.domain.agency.Departure;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.city.CityRepository;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddRouteService implements AddRouteUseCase {
    private final AgencyRepository agencyRepository;
    private final CityRepository cityRepository;

    @Override
    public Route execute(UUID agencyId, String departure, String arrival, double price, int totalSeats) {
        var agency = agencyRepository.loadById(new AgencyId(agencyId));
        Route route = agency.addRoute(
                new Departure(resolveCityName(departure)),
                new Arrival(resolveCityName(arrival)),
                new RoutePrice(BigDecimal.valueOf(price)));
        agencyRepository.insert(agency);
        return route;
    }

    private String resolveCityName(String cityId) {
        try {
            return cityRepository
                    .findById(UUID.fromString(cityId))
                    .map(city -> city.getName().value())
                    .orElseThrow(() -> new DomainException(AgencyErrorCode.CITY_404_001));
        } catch (IllegalArgumentException ex) {
            throw new DomainException(AgencyErrorCode.CITY_404_001);
        }
    }

    public record Command(UUID agencyId, String departure, String arrival, double price, int totalSeats) {}
}
