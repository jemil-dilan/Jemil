package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Route;
import java.util.UUID;

public interface AddRouteUseCase {
    Route execute(UUID agencyId, String departure, String arrival, double price, int totalSeats);
}
