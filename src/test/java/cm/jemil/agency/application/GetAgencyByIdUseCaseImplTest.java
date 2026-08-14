package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCaseImpl;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.agency.domain.agency.views.AgencyView;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import cm.jemil.shared.utils.CreatedAt;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GetAgencyByIdUseCaseImplTest {

    @Mock
    private AgencyRepository agencyRepository;

    private GetAgencyByIdUseCaseImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GetAgencyByIdUseCaseImpl(agencyRepository);
    }

    @Test
    void shouldReturnAgencyWhenFound() {
        var id = AgencyId.generate();
        var agency = Agency.of(new AgencyName("Test"), new PhoneNumber("1", "2"), new LicenceNumber("boo"));
        var view = new AgencyView.AgencyView1(
                agency.getId(),
                agency.getName(),
                agency.getPhoneNumber(),
                agency.getStatus(),
                List.of(),
                agency.getLicenseNumber(),
                new CreatedAt());
        when(agencyRepository.loadByIdAgencyView1(any())).thenReturn(view);

        var result = service.execute(id.value());

        assertThat(result).isNotNull();
        assertThat(result.name().value()).isEqualTo("Test");
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        var id = AgencyId.generate();
        when(agencyRepository.loadByIdAgencyView1(any()))
                .thenThrow(new DomainException(AgencyErrorCode.AGENCY_404_001));

        assertThatThrownBy(() -> service.execute(id.value())).isInstanceOf(DomainException.class);
    }
}
