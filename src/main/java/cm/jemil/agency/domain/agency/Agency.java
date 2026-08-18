package cm.jemil.agency.domain.agency;

import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.shared.utils.CreatedAt;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Agency {
    private final AgencyId id;
    private AgencyName name;
    private AgencyStatus status;
    private PhoneNumber phoneNumber;
    private LicenceNumber licenseNumber;
    private final List<AgencyBranch> branches;
    private final List<Route> routes;
    private CreatedAt createdAt;

    public static Agency of(AgencyName name, PhoneNumber phoneNumber, LicenceNumber licenseNumber) {
        return new Agency(
                AgencyId.generate(),
                name,
                AgencyStatus.ACTIVE,
                phoneNumber,
                licenseNumber,
                new ArrayList<>(),
                new ArrayList<>(),
                CreatedAt.now());
    }

    public void suspend() {
        this.status = AgencyStatus.SUSPENDED;
    }

    public void activate() {
        this.status = AgencyStatus.ACTIVE;
    }

    public void addRoute(CityId departure, CityId arrival, RoutePrice price, TotalSeats totalSeats) {
        Route route = Route.create(departure, arrival, price, totalSeats);
        routes.add(route);
    }

    public UUID id() {
        return id.value();
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }
}
