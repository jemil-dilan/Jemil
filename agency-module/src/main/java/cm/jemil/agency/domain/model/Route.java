package cm.jemil.agency.domain.model;

import cm.jemil.agency.domain.exception.AgencyDomainException;
import java.util.Objects;

/**
 * Entity : Route.
 *
 * <p>Une Route appartient à une Agency (via agencyId).
 * Elle n'est pas un Aggregate Root — on y accède toujours via Agency.
 */
public class Route {

    private final RouteId id;
    private final AgencyId agencyId;
    private final String origin;
    private final String destination;
    private final int totalSeats;
    private boolean active;

    private Route(RouteId id, AgencyId agencyId, String origin, String destination, int totalSeats) {
        this.id = id;
        this.agencyId = agencyId;
        this.origin = origin;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.active = true;
    }

    static Route create(RouteId id, AgencyId agencyId, String origin, String destination, int totalSeats) {
        validate(origin, destination, totalSeats);
        return new Route(id, agencyId, origin, destination, totalSeats);
    }

    public static Route reconstitute(
        RouteId id, AgencyId agencyId, String origin, String destination, int totalSeats, boolean active) {
        Route route = new Route(id, agencyId, origin, destination, totalSeats);
        route.active = active;
        return route;
    }

    public void deactivate() {
        this.active = false;
    }

    private static void validate(String origin, String destination, int totalSeats) {
        if (origin == null || origin.isBlank()) {
            throw new AgencyDomainException("La ville de départ est obligatoire.");
        }
        if (destination == null || destination.isBlank()) {
            throw new AgencyDomainException("La ville d'arrivée est obligatoire.");
        }
        if (origin.equalsIgnoreCase(destination)) {
            throw new AgencyDomainException("Départ et destination ne peuvent pas être identiques.");
        }
        if (totalSeats <= 0 || totalSeats > 100) {
            throw new AgencyDomainException("Le nombre de places doit être entre 1 et 100.");
        }
    }

    public RouteId getId() {
        return id;
    }

    public AgencyId getAgencyId() {
        return agencyId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route route)) return false;
        return Objects.equals(id, route.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
