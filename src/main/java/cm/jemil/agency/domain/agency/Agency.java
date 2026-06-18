package cm.jemil.agency.domain.agency;

import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Agency {
    private final AgencyId id;
    private String name;
    private AgencyStatus status;
    private Address address;
    private PhoneNumber phoneNumber;
    private final List<Route> routes;

    public static Agency register(String name, Address address, PhoneNumber phoneNumber) {
        return new Agency(AgencyId.generate(), name, AgencyStatus.ACTIVE, address, phoneNumber, new ArrayList<>());
    }

    public void suspend() {
        this.status = AgencyStatus.SUSPENDED;
    }

    public void activate() {
        this.status = AgencyStatus.ACTIVE;
    }

    public void addRoute(String departure, String arrival, double price) {
        routes.add(new Route(RouteId.generate(), departure, arrival, price, new ArrayList<>()));
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }
}
