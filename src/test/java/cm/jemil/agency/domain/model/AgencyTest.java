package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.Arrival;
import cm.jemil.agency.domain.agency.CommissionRate;
import cm.jemil.agency.domain.agency.Departure;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.shared.utils.PhoneNumber;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AgencyTest {

    @Test
    void shouldCreateAgencyWhenRegistering() {
        var agency = Agency.of(
                new AgencyName("Global Voyages"),
                new PhoneNumber("237", "653492410"),
                new LicenceNumber("sjoiaj"),
                new CommissionRate(3.0));

        assertThat(agency.getId()).isNotNull();
        assertThat(agency.getName().value()).isEqualTo("Global Voyages");
        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
        assertThat(agency.getPhoneNumber().fullNumber()).isEqualTo("+237653492410");
        assertThat(agency.getRoutes()).isEmpty();
    }

    @Test
    void shouldSuspendAgency() {
        var agency = Agency.of(new AgencyName("Test"), new PhoneNumber("1", "2"), new LicenceNumber("boo"), new CommissionRate(2.0));

        agency.suspend();

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.SUSPENDED);
    }

    @Test
    void shouldActivateAgency() {
        var agency = Agency.of(new AgencyName("Test"), new PhoneNumber("1", "2"), new LicenceNumber("boo"), new CommissionRate(2.0));
        agency.suspend();

        agency.activate();

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldAddRouteToAgency() {
        var agency = Agency.of(new AgencyName("Test"), new PhoneNumber("1", "2"), new LicenceNumber("boo"), new CommissionRate(2.0));

        agency.addRoute(new Departure("Douala"), new Arrival("Yaoundé"), new RoutePrice(BigDecimal.valueOf(5000)));

        assertThat(agency.getRoutes()).hasSize(1);
        var route = agency.getRoutes().getFirst();
        assertThat(route.getDeparture().value()).isEqualTo("Douala");
        assertThat(route.getArrival().value()).isEqualTo("Yaoundé");
        assertThat(route.getPrice().price()).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }

    @Test
    void shouldGenerateUniqueIdsOnEachRegistration() {
        var agency1 = Agency.of(new AgencyName("A"), new PhoneNumber("1", "2"), new LicenceNumber("boo"), new CommissionRate(2.0));
        var agency2 = Agency.of(new AgencyName("B"), new PhoneNumber("1", "2"), new LicenceNumber("3002"), new CommissionRate(2.0));

        assertThat(agency1.getId()).isNotEqualTo(agency2.getId());
    }
}
