package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdService;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GetAgencyByIdServiceTest {

    @Mock
    private AgencyRepository agencyRepository;

    private GetAgencyByIdService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GetAgencyByIdService(agencyRepository);
    }

    @Test
    void shouldReturnAgencyWhenFound() {
        var id = AgencyId.generate();
        var agency = Agency.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));
        when(agencyRepository.findById(id)).thenReturn(Optional.of(agency));

        var result = service.execute(id);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test");
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        var id = AgencyId.generate();
        when(agencyRepository.findById(id)).thenReturn(Optional.empty());

        var result = service.execute(id);

        assertThat(result).isEmpty();
    }
}
