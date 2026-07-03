package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.Route;

public interface AddRouteUseCase {
    Route execute(AgencyId agencyId, String departure, String arrival, double price, int totalSeats);
}
