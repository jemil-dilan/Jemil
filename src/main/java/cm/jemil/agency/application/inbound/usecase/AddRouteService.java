package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.Route;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddRouteService implements AddRouteUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public Route execute(AgencyId agencyId, String departure, String arrival, double price, int totalSeats) {
        var agency = agencyRepository.loadById(agencyId);
        Route route = agency.addRoute(departure, arrival, price, totalSeats);
        agencyRepository.insert(agency);
        return route;
    }
}
