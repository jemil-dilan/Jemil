package cm.jemil.agency.domain.port.in;

import cm.jemil.agency.domain.model.AgencyId;
import cm.jemil.agency.domain.model.Route;
import java.util.Optional;

/** Port entrant : ajout de route à une agence existante. */
public interface AddRouteUseCase {

    Optional<Route> addRoute(AgencyId agencyId, AddRouteCommand command);

    record AddRouteCommand(String origin, String destination, int totalSeats) {

        public AddRouteCommand {
            if (origin == null || origin.isBlank()) {
                throw new IllegalArgumentException("La ville de départ est obligatoire.");
            }
            if (destination == null || destination.isBlank()) {
                throw new IllegalArgumentException("La ville d'arrivée est obligatoire.");
            }
            if (totalSeats <= 0) {
                throw new IllegalArgumentException("Le nombre de places doit être supérieur à zéro.");
            }
        }
    }
}
