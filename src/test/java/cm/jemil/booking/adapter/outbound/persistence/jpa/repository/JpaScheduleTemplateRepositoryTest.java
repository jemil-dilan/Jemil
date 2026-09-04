package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.booking.domain.trip.ActiveScheduleTemplate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class JpaScheduleTemplateRepositoryTest {

    @Mock
    private ScheduleTemplateSpringRepository scheduleTemplateSpringRepository;

    private JpaScheduleTemplateRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new JpaScheduleTemplateRepository(scheduleTemplateSpringRepository);
    }

    @Test
    void findActiveTemplatesDelegatesToSpringRepository() {
        var template = new ActiveScheduleTemplate(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalTime.of(8, 30),
                (short) 0b0000001,
                4500,
                "ECONOMY",
                50);
        when(scheduleTemplateSpringRepository.findActiveTemplates()).thenReturn(List.of(template));

        var result = repository.findActiveTemplates();

        assertThat(result).containsExactly(template);
        verify(scheduleTemplateSpringRepository).findActiveTemplates();
    }

    @Test
    void findActiveTemplatesReturnsEmptyListWhenNoneActive() {
        when(scheduleTemplateSpringRepository.findActiveTemplates()).thenReturn(List.of());

        var result = repository.findActiveTemplates();

        assertThat(result).isEmpty();
        verify(scheduleTemplateSpringRepository).findActiveTemplates();
    }
}
