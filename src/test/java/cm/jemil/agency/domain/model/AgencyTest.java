package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import org.junit.jupiter.api.Test;

class AgencyTest {

    @Test
    void shouldCreateAgencyWhenRegistering() {
        var agency = Agency.register(
                "Global Voyages", new Address("Douala", "Bonanjo"), new PhoneNumber("237", "653492410"));

        assertThat(agency.getId()).isNotNull();
        assertThat(agency.getName()).isEqualTo("Global Voyages");
        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
        assertThat(agency.getAddress().city()).isEqualTo("Douala");
        assertThat(agency.getPhoneNumber().fullNumber()).isEqualTo("+237653492410");
        assertThat(agency.getRoutes()).isEmpty();
    }

    @Test
    void shouldSuspendAgency() {
        var agency = Agency.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));

        agency.suspend();

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.SUSPENDED);
    }

    @Test
    void shouldActivateAgency() {
        var agency = Agency.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));
        agency.suspend();

        agency.activate();

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldAddRouteToAgency() {
        var agency = Agency.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));

        agency.addRoute("Douala", "Yaoundé", 5000);

        assertThat(agency.getRoutes()).hasSize(1);
        var route = agency.getRoutes().getFirst();
        assertThat(route.getDeparture()).isEqualTo("Douala");
        assertThat(route.getArrival()).isEqualTo("Yaoundé");
        assertThat(route.getPrice()).isEqualTo(5000);
    }

    @Test
    void shouldGenerateUniqueIdsOnEachRegistration() {
        var agency1 = Agency.register("A", new Address("X", "Y"), new PhoneNumber("1", "2"));
        var agency2 = Agency.register("B", new Address("X", "Y"), new PhoneNumber("1", "2"));

        assertThat(agency1.getId()).isNotEqualTo(agency2.getId());
    }
}
