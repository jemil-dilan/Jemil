package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.SuspendAgencyUseCaseImpl;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import cm.jemil.shared.utils.PhoneNumber;
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
                new AgencyName("Test Agency"), new PhoneNumber("237", "653492410"), new LicenceNumber("LIC-123"));

        when(agencyRepository.loadById(agencyId)).thenReturn(agency);

        service.execute(agencyId.value());

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.SUSPENDED);
        verify(agencyRepository).loadById(agencyId);
        verify(agencyRepository).update(agency);
    }

    @Test
    void shouldThrowWhenAgencyNotFound() {
        var agencyId = new AgencyId(UUID.randomUUID());

        when(agencyRepository.loadById(any())).thenThrow(new DomainException(AgencyErrorCode.AGENCY_404_001));

        assertThatThrownBy(() -> service.execute(agencyId.value()))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("code", AgencyErrorCode.AGENCY_404_001.getCode());
    }

    @Test
    void shouldUpdateStatusToSuspended() {
        var agencyId = new AgencyId(UUID.randomUUID());
        var agency = Agency.of(
                new AgencyName("Active Agency"), new PhoneNumber("237", "653492410"), new LicenceNumber("LIC-456"));

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
        when(agencyRepository.loadById(agencyId)).thenReturn(agency);

        service.execute(agencyId.value());

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.SUSPENDED);
    }
}
