package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.shared.utils.PhoneNumber;
import org.junit.jupiter.api.Test;

class AgencyTest {

    @Test
    void shouldCreateAgencyWhenRegistering() {
        var agency = Agency.of("Global Voyages", new PhoneNumber("237", "653492410"), "sjoiaj", 3.0);

        assertThat(agency.getId()).isNotNull();
        assertThat(agency.getName()).isEqualTo("Global Voyages");
        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
        assertThat(agency.getPhoneNumber().fullNumber()).isEqualTo("+237653492410");
        assertThat(agency.getRoutes()).isEmpty();
    }

    @Test
    void shouldSuspendAgency() {
        var agency = Agency.of("Test", new PhoneNumber("1", "2"), "boo", 2.0);

        agency.suspend();

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.SUSPENDED);
    }

    @Test
    void shouldActivateAgency() {
        var agency = Agency.of("Test", new PhoneNumber("1", "2"), "boo", 2.0);
        agency.suspend();

        agency.activate();

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldAddRouteToAgency() {
        var agency = Agency.of("Test", new PhoneNumber("1", "2"), "boo", 2.0);

        agency.addRoute("Douala", "Yaoundé", 5000);

        assertThat(agency.getRoutes()).hasSize(1);
        var route = agency.getRoutes().getFirst();
        assertThat(route.getDeparture()).isEqualTo("Douala");
        assertThat(route.getArrival()).isEqualTo("Yaoundé");
        assertThat(route.getPrice()).isEqualTo(5000);
    }

    @Test
    void shouldGenerateUniqueIdsOnEachRegistration() {
        var agency1 = Agency.of("A", new PhoneNumber("1", "2"), "boo", 2.0);
        var agency2 = Agency.of("B", new PhoneNumber("1", "2"), "3002", 2.0);

        assertThat(agency1.getId()).isNotEqualTo(agency2.getId());
    }
}
