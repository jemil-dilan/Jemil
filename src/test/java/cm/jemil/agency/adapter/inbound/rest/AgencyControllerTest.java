package cm.jemil.agency.adapter.inbound.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCase;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCase;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RegisterAgencyDTO;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.List;
import java.util.Optional;
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

    private final AgencyRestMapper restMapper = new AgencyRestMapperImpl();

    private AgencyController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller =
                new AgencyController(registerAgencyUseCase, getAgencyByIdUseCase, getAllAgenciesUseCase, restMapper);
    }

    @Test
    void shouldRegisterAgency() {
        var id = AgencyId.generate();
        var dto = new RegisterAgencyDTO();
        var addressDto = new cm.jemil.generated.agency.adapter.rest.inbound.dto.AddressDTO();
        addressDto.setCity("Douala");
        addressDto.setDistrict("Bonanjo");
        dto.setAddress(addressDto);
        var phoneDto = new cm.jemil.generated.agency.adapter.rest.inbound.dto.PhoneNumberDTO();
        phoneDto.setCountryCode("237");
        phoneDto.setNumber("653492410");
        dto.setPhoneNumber(phoneDto);
        dto.setName("Global Voyages");
        when(registerAgencyUseCase.register(any(), any(), any())).thenReturn(id);

        var response = controller.registerAgency(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNewId()).isEqualTo(id.value());
    }

    @Test
    void shouldReturnAgencyWhenFoundById() {
        var uuid = UUID.randomUUID();
        var agency = Agency.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));
        when(getAgencyByIdUseCase.execute(new AgencyId(uuid))).thenReturn(Optional.of(agency));

        var response = controller.getAgencyById(uuid);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test");
    }

    @Test
    void shouldReturn404WhenNotFound() {
        var uuid = UUID.randomUUID();
        when(getAgencyByIdUseCase.execute(new AgencyId(uuid))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getAgencyById(uuid))
                .isInstanceOf(cm.jemil.agency.domain.exception.AgencyNotFoundException.class);
    }

    @Test
    void shouldReturnAllAgencies() {
        var agency = Agency.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));
        when(getAllAgenciesUseCase.execute(null)).thenReturn(List.of(agency));

        var response = controller.getAllAgencies(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().getName()).isEqualTo("Test");
    }

    @Test
    void shouldReturnEmptyListWhenNoAgencies() {
        when(getAllAgenciesUseCase.execute(null)).thenReturn(List.of());

        var response = controller.getAllAgencies(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}
