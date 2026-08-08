package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.SuspendAgencyUseCaseImpl;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.CommissionRate;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import cm.jemil.shared.utils.CreatedAt;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SuspendAgencyUseCaseImplTest {

    @Mock
    private AgencyRepository agencyRepository;

    private SuspendAgencyUseCaseImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SuspendAgencyUseCaseImpl(agencyRepository);
    }

    @Test
    void shouldSuspendAgency() {
        var agencyId = new AgencyId(UUID.randomUUID());
        var agency = Agency.of(
                new AgencyName("Test Agency"),
                new PhoneNumber("237", "653492410"),
                new LicenceNumber("LIC-123"),
                new CommissionRate(5.0));
        
        var agencyViewActive = new AgencyView1(
                agencyId,
                "Test Agency",
                new PhoneNumber("237", "653492410"),
                AgencyStatus.ACTIVE,
                List.of(),
                "LIC-123",
                5.0,
                new CreatedAt());

        var agencyViewSuspended = new AgencyView1(
                agencyId,
                "Test Agency",
                new PhoneNumber("237", "653492410"),
                AgencyStatus.SUSPENDED,
                List.of(),
                "LIC-123",
                5.0,
                new CreatedAt());

        when(agencyRepository.loadById(agencyId)).thenReturn(agency);
        when(agencyRepository.loadByIdAgencyView1(agencyId)).thenReturn(agencyViewSuspended);

        var result = service.execute(agencyId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(AgencyStatus.SUSPENDED);
        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.SUSPENDED);
        verify(agencyRepository).loadById(agencyId);
        verify(agencyRepository).save(agency);
        verify(agencyRepository).loadByIdAgencyView1(agencyId);
    }

    @Test
    void shouldThrowWhenAgencyNotFound() {
        var agencyId = new AgencyId(UUID.randomUUID());
        
        when(agencyRepository.loadById(agencyId))
                .thenThrow(new DomainException(AgencyErrorCode.AGENCY_404_001));

        assertThatThrownBy(() -> service.execute(agencyId))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("code", AgencyErrorCode.AGENCY_404_001.getCode());
    }

    @Test
    void shouldUpdateStatusToSuspended() {
        var agencyId = new AgencyId(UUID.randomUUID());
        var agency = Agency.of(
                new AgencyName("Active Agency"),
                new PhoneNumber("237", "653492410"),
                new LicenceNumber("LIC-456"),
                new CommissionRate(10.0));
        
        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);

        var agencyViewSuspended = new AgencyView1(
                agencyId,
                "Active Agency",
                new PhoneNumber("237", "653492410"),
                AgencyStatus.SUSPENDED,
                List.of(),
                "LIC-456",
                10.0,
                new CreatedAt());

        when(agencyRepository.loadById(agencyId)).thenReturn(agency);
        when(agencyRepository.loadByIdAgencyView1(agencyId)).thenReturn(agencyViewSuspended);

        var result = service.execute(agencyId);

        assertThat(result.status()).isEqualTo(AgencyStatus.SUSPENDED);
        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.SUSPENDED);
    }
}
