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
    private AgencyName name;
    private AgencyStatus status;
    private PhoneNumber phoneNumber;
    private LicenceNumber licenseNumber;
    private CommissionRate commissionRate;
    private final List<AgencyBranch> branches;
    private final List<Route> routes;
    private CreatedAt createdAt;

    public static Agency of(
            AgencyName name, 
            PhoneNumber phoneNumber, 
            LicenceNumber licenseNumber, 
            CommissionRate commissionRate) {
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

    public Route addRoute(Departure departure, Arrival arrival, RoutePrice price) {
        Route route = Route.create(departure, arrival, price);
        routes.add(route);
        return route;
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    /**
     * Updates the commission rate for this agency.
     */
    public void updateCommissionRate(CommissionRate newRate) {
        this.commissionRate = newRate;
    }

    /**
     * Returns the commission rate as a double for compatibility with existing code.
     */
    public double getCommissionRateValue() {
        return commissionRate.value();
    }
}
