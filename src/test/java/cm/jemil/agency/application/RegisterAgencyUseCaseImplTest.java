package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCaseImpl;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.shared.outbox.OutboxEventPublisher;
import cm.jemil.shared.utils.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RegisterAgencyUseCaseImplTest {

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private OutboxEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<Agency> agencyCaptor;

    private RegisterAgencyUseCaseImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RegisterAgencyUseCaseImpl(agencyRepository, eventPublisher);
    }

    @Test
    void shouldExecuteAgency() {
        var phoneNumber = new PhoneNumber("237", "653492410");

        var command = new RegisterAgencyUseCaseImpl.Command("Global Voyages", phoneNumber, "", 0.0);
        var id = service.execute(command);

        assertThat(id).isNotNull();
        assertThat(id.value()).isNotNull();
    }

    @Test
    void shouldSaveAgencyToRepository() {
        var phoneNumber = new PhoneNumber("237", "653492410");

        var command = new RegisterAgencyUseCaseImpl.Command("Global Voyages", phoneNumber, "", 0.0);
        service.execute(command);

        verify(agencyRepository).insert(agencyCaptor.capture());
        var saved = agencyCaptor.getValue();
        assertThat(saved.getName()).isEqualTo("Global Voyages");
        assertThat(saved.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(saved.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldPublishEventAfterRegistration() {
        var command2 = new RegisterAgencyUseCaseImpl.Command("Test", new PhoneNumber("1", "2"), "", 0.0);
        service.execute(command2);

        verify(eventPublisher).publish(any());
    }
}
