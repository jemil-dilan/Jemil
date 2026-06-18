package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import cm.jemil.agency.application.inbound.usecase.RegisterAgencyService;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.shared.outbox.OutboxEventPublisher;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RegisterAgencyServiceTest {

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private OutboxEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<Agency> agencyCaptor;

    private RegisterAgencyService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RegisterAgencyService(agencyRepository, eventPublisher);
    }

    @Test
    void shouldRegisterAgency() {
        var address = new Address("Douala", "Bonanjo");
        var phoneNumber = new PhoneNumber("237", "653492410");

        var id = service.register("Global Voyages", address, phoneNumber);

        assertThat(id).isNotNull();
        assertThat(id.value()).isNotNull();
    }

    @Test
    void shouldSaveAgencyToRepository() {
        var address = new Address("Douala", "Bonanjo");
        var phoneNumber = new PhoneNumber("237", "653492410");

        service.register("Global Voyages", address, phoneNumber);

        verify(agencyRepository).save(agencyCaptor.capture());
        var saved = agencyCaptor.getValue();
        assertThat(saved.getName()).isEqualTo("Global Voyages");
        assertThat(saved.getAddress()).isEqualTo(address);
        assertThat(saved.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(saved.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldPublishEventAfterRegistration() {
        service.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));

        verify(eventPublisher).publish(any());
    }
}
