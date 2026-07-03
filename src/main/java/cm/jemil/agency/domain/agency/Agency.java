package cm.jemil.agency.domain.agency;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_003;
import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_005;

import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.shared.exception.DomainException;
import cm.jemil.shared.utils.CreatedAt;
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
    private PhoneNumber phoneNumber;
    private String licenseNumber;
    private double commissionRate;
    private final List<AgencyBranch> branches;
    private final List<Route> routes;
    private CreatedAt createdAt;

    public static Agency of(String name, PhoneNumber phoneNumber, String licenseNumber, double commissionRate) {
        validateRequired(name, phoneNumber);
        return new Agency(
                AgencyId.generate(),
                name,
                AgencyStatus.ACTIVE,
                phoneNumber,
                licenseNumber,
                commissionRate,
                new ArrayList<>(),
                new ArrayList<>(),
                new CreatedAt());
    }

    public void suspend() {
        this.status = AgencyStatus.SUSPENDED;
    }

    public void activate() {
        this.status = AgencyStatus.ACTIVE;
    }

    public Route addRoute(String departure, String arrival, double price) {
        Route route = Route.create(departure, arrival, price);
        routes.add(route);
        return route;
    }

    public Route addRoute(String departure, String arrival, double price, int totalSeats) {
        Route route = Route.create(departure, arrival, price, totalSeats);
        routes.add(route);
        return route;
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    private static void validateRequired(String name, PhoneNumber phoneNumber) {
        if (name == null || name.isBlank()) {
            throw new DomainException(AGENCY_400_003);
        }
        if (phoneNumber == null
                || phoneNumber.countryCode() == null
                || phoneNumber.countryCode().isBlank()
                || phoneNumber.number() == null
                || phoneNumber.number().isBlank()) {
            throw new DomainException(AGENCY_400_005);
        }
    }
}
