package cm.jemil.agency.adapter.inbound.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.AddBranchUseCase;
import cm.jemil.agency.application.inbound.usecase.AddRouteUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCase;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCase;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.RouteId;
import cm.jemil.agency.domain.agency.views.AgencyView;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AddRouteDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateAgencyDTO;
import cm.jemil.shared.exception.DomainException;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class AgencyControllerTest {

    @Mock
    private RegisterAgencyUseCase registerAgencyUseCase;

    @Mock
    private GetAgencyByIdUseCase getAgencyByIdUseCase;

    @Mock
    private GetAllAgenciesUseCase getAllAgenciesUseCase;

    @Mock
    private AddRouteUseCase addRouteUseCase;

    @Mock
    private AddBranchUseCase addBranchUseCase;

    private final AgencyRestMapper restMapper = new AgencyRestMapperImpl();

    private AgencyController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AgencyController(
                registerAgencyUseCase,
                getAgencyByIdUseCase,
                getAllAgenciesUseCase,
                addRouteUseCase,
                addBranchUseCase,
                restMapper);
    }

    @Test
    void shouldOfAgency() {
        var id = AgencyId.generate();
        var dto = new CreateAgencyDTO();
        var phoneDto = new cm.jemil.generated.agency.adapter.rest.inbound.dto.PhoneNumberDTO();
        phoneDto.setCountryCode("237");
        phoneDto.setNumber("653492410");
        dto.setPhoneNumber(phoneDto);
        dto.setName("Global Voyages");
        dto.setLicenseNumber("LIC-001");
        dto.setCommissionRate(4.0);
        when(registerAgencyUseCase.execute(any())).thenReturn(id);

        var response = controller.registerAgency(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNewId()).isEqualTo(id.value());
    }

    @Test
    void shouldReturnAgencyWhenFoundById() {
        var uuid = UUID.randomUUID();
        var agency = Agency.of("Test", new PhoneNumber("1", "2"), "boo", 2.0);
        var view = new AgencyView.AgencyView1(
                agency.getId(),
                agency.getName(),
                agency.getPhoneNumber(),
                agency.getStatus(),
                List.of(),
                agency.getLicenseNumber(),
                agency.getCommissionRate(),
                agency.getCreatedAt());
        when(getAgencyByIdUseCase.execute(uuid)).thenReturn(view);

        var response = controller.getAgencyById(uuid);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test");
    }

    @Test
    void shouldReturn404WhenNotFound() {
        var uuid = UUID.randomUUID();
        when(getAgencyByIdUseCase.execute(uuid)).thenThrow(new DomainException(AgencyErrorCode.AGENCY_404_001));

        assertThatThrownBy(() -> controller.getAgencyById(uuid)).isInstanceOf(DomainException.class);
    }

    @Test
    void shouldReturnAllAgencies() {
        var agency = Agency.of("Test", new PhoneNumber("1", "2"), "boo", 2.0);
        var view = new AgencyView.AgencyView1(
                agency.getId(),
                agency.getName(),
                agency.getPhoneNumber(),
                agency.getStatus(),
                List.of(),
                agency.getLicenseNumber(),
                agency.getCommissionRate(),
                agency.getCreatedAt());
        when(getAllAgenciesUseCase.execute(null, 0, 20)).thenReturn(List.of(view));

        var response = controller.getAllAgencies(null, 0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
    }

    @Test
    void shouldReturnEmptyListWhenNoAgencies() {
        when(getAllAgenciesUseCase.execute(null, 0, 20)).thenReturn(List.of());

        var response = controller.getAllAgencies(null, 0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isEmpty();
    }

    @Test
    void shouldAddRoute() {
        var agencyId = UUID.randomUUID();
        var originCityId = UUID.randomUUID();
        var destinationCityId = UUID.randomUUID();
        var route = new Route(RouteId.generate(), "Douala", "Yaounde", 5000, 70, new ArrayList<>());
        var dto = new AddRouteDTO();
        dto.setOriginCityId(originCityId);
        dto.setDestinationCityId(destinationCityId);
        dto.setPrice(5000.0);
        dto.setTotalSeats(70);
        when(addRouteUseCase.execute(
                        new AgencyId(agencyId), originCityId.toString(), destinationCityId.toString(), 5000.0, 70))
                .thenReturn(route);

        var response = controller.addRouteToAgency(agencyId, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAgencyId()).isEqualTo(agencyId);
        assertThat(response.getBody().getTotalSeats()).isEqualTo(70);
    }
}
