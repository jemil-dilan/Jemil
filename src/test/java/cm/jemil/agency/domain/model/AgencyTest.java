package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.shared.utils.PhoneNumber;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgencyTest {

    @Test
    void shouldCreateAgencyWhenRegistering() {
        var agency = Agency.of(
                new AgencyName("Global Voyages"), new PhoneNumber("237", "653492410"), new LicenceNumber("sjoiaj"));

        assertThat(agency.getId()).isNotNull();
        assertThat(agency.getName().value()).isEqualTo("Global Voyages");
        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
        assertThat(agency.getPhoneNumber().fullNumber()).isEqualTo("+237653492410");
        assertThat(agency.getRoutes()).isEmpty();
    }

    @Test
    void shouldSuspendAgency() {
        var agency = Agency.of(new AgencyName("Test"), new PhoneNumber("1", "2"), new LicenceNumber("boo"));

        agency.suspend();

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.SUSPENDED);
    }

    @Test
    void shouldActivateAgency() {
        var agency = Agency.of(new AgencyName("Test"), new PhoneNumber("1", "2"), new LicenceNumber("boo"));
        agency.suspend();

        agency.activate();

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldAddRouteToAgency() {
        var agency = Agency.of(new AgencyName("Test"), new PhoneNumber("1", "2"), new LicenceNumber("boo"));
        var departure = new CityId(UUID.randomUUID());
        var arrival = new CityId(UUID.randomUUID());

        agency.addRoute(departure, arrival, new RoutePrice(BigDecimal.valueOf(5000)));

        assertThat(agency.getRoutes()).hasSize(1);
        var route = agency.getRoutes().getFirst();
        assertThat(route.getDeparture()).isEqualTo(departure);
        assertThat(route.getArrival()).isEqualTo(arrival);
        assertThat(route.getPrice().price()).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }

    @Test
    void shouldGenerateUniqueIdsOnEachRegistration() {
        var agency1 = Agency.of(new AgencyName("A"), new PhoneNumber("1", "2"), new LicenceNumber("boo"));
        var agency2 = Agency.of(new AgencyName("B"), new PhoneNumber("1", "2"), new LicenceNumber("3002"));

        assertThat(agency1.getId()).isNotEqualTo(agency2.getId());
    }
}
