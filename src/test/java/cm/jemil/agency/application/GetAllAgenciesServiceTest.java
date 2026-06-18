package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesService;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GetAllAgenciesServiceTest {

    @Mock
    private AgencyRepository agencyRepository;

    private GetAllAgenciesService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GetAllAgenciesService(agencyRepository);
    }

    @Test
    void shouldReturnAllAgencies() {
        var agencies = List.of(
                Agency.register("A", new Address("X", "Y"), new PhoneNumber("1", "2")),
                Agency.register("B", new Address("X", "Y"), new PhoneNumber("1", "2")));
        when(agencyRepository.findAll()).thenReturn(agencies);

        var result = service.execute(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("A");
        assertThat(result.get(1).getName()).isEqualTo("B");
    }

    @Test
    void shouldReturnEmptyListWhenNoAgencies() {
        when(agencyRepository.findAll()).thenReturn(List.of());

        var result = service.execute(null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterAgenciesByCityIgnoringCase() {
        var agencies = List.of(
                Agency.register("Douala Agency", new Address("Douala", "Akwa"), new PhoneNumber("237", "1")),
                Agency.register("Yaounde Agency", new Address("Yaounde", "Bastos"), new PhoneNumber("237", "2")));
        when(agencyRepository.findAll()).thenReturn(agencies);

        var result = service.execute("douala");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Douala Agency");
    }

    @Test
    void shouldReturnOnlyActiveAgencies() {
        var active = Agency.register("Active", new Address("Douala", "Akwa"), new PhoneNumber("237", "1"));
        var suspended = Agency.register("Suspended", new Address("Douala", "Bonanjo"), new PhoneNumber("237", "2"));
        suspended.suspend();
        when(agencyRepository.findAll()).thenReturn(List.of(active, suspended));

        var result = service.execute(null);

        assertThat(result).containsExactly(active);
    }
}
