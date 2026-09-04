package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.AddRouteUseCaseImpl;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AddRouteUseCaseImplTest {

    @Mock
    private AgencyRepository agencyRepository;

    private AddRouteUseCaseImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AddRouteUseCaseImpl(agencyRepository);
    }

    private Agency agency() {
        return Agency.of(
                new AgencyName("Global Voyages"), new PhoneNumber("237", "653492410"), new LicenceNumber("sjoiaj"));
    }

    @Test
    void shouldReturnTheNewRouteId() {
        var agency = agency();
        when(agencyRepository.loadById(any(AgencyId.class))).thenReturn(agency);
        var command = new AddRouteUseCaseImpl.Command(agency.id(), UUID.randomUUID(), UUID.randomUUID(), 5000, 40);

        UUID routeId = service.execute(command);

        assertThat(routeId).isNotNull();
        assertThat(agency.getRoutes()).hasSize(1);
        assertThat(agency.getRoutes().get(0).id()).isEqualTo(routeId);
    }

    @Test
    void shouldPersistAgencyAfterAddingRoute() {
        var agency = agency();
        when(agencyRepository.loadById(any(AgencyId.class))).thenReturn(agency);
        var command = new AddRouteUseCaseImpl.Command(agency.id(), UUID.randomUUID(), UUID.randomUUID(), 4500, 30);

        service.execute(command);

        verify(agencyRepository).update(agency);
    }

    @Test
    void shouldMapCommandValuesToRoute() {
        var agency = agency();
        when(agencyRepository.loadById(any(AgencyId.class))).thenReturn(agency);
        var origin = UUID.randomUUID();
        var destination = UUID.randomUUID();

        var routeId = service.execute(new AddRouteUseCaseImpl.Command(agency.id(), origin, destination, 6000, 55));

        var route = agency.getRoutes().stream()
                .filter(r -> r.id().equals(routeId))
                .findFirst()
                .orElseThrow();
        assertThat(route.getDeparture().value()).isEqualTo(origin);
        assertThat(route.getArrival().value()).isEqualTo(destination);
        assertThat(route.getPrice().amountXaf()).isEqualTo(6000);
        assertThat(route.getTotalSeats().value()).isEqualTo(55);
    }
}
