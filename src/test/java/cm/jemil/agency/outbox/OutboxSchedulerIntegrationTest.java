package cm.jemil.agency.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRegisteredEvent;
import cm.jemil.shared.events.DomainEventType;
import cm.jemil.shared.outbox.JpaOutboxEventPublisher;
import cm.jemil.shared.outbox.OutboxEvent;
import cm.jemil.shared.outbox.OutboxEventPublisher;
import cm.jemil.shared.outbox.OutboxEventStatus;
import cm.jemil.shared.outbox.OutboxRepository;
import cm.jemil.shared.outbox.OutboxScheduler;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import tools.jackson.databind.ObjectMapper;

class OutboxSchedulerIntegrationTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ObjectMapper objectMapper;

    private OutboxEventPublisher outboxEventPublisher;

    private OutboxScheduler outboxScheduler;

    @Captor
    private ArgumentCaptor<OutboxEvent> eventCaptor;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();

        outboxEventPublisher = new JpaOutboxEventPublisher(outboxRepository, objectMapper);

        List<DomainEventType> domainEventTypes = List.of(DomainEventType.from(AgencyRegisteredEvent.class));
        outboxScheduler = new OutboxScheduler(outboxRepository, eventPublisher, objectMapper, domainEventTypes);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void shouldPublishAndProcessOutboxEvent() throws Exception {
        var event = AgencyRegisteredEvent.of(AgencyId.generate(), "Test Agency");
        outboxEventPublisher.publish(event);

        verify(outboxRepository).save(eventCaptor.capture());
        var saved = eventCaptor.getValue();
        assertThat(saved.getEventType()).isEqualTo("AgencyRegisteredEvent");
        assertThat(saved.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
        assertThat(saved.getAggregateType()).isEqualTo("Agency");
    }

    @Test
    void shouldMarkEventAsSentAfterProcessing() throws Exception {
        var event = AgencyRegisteredEvent.of(AgencyId.generate(), "Test Agency");

        var outboxEvent = new OutboxEvent(
                UUID.randomUUID(),
                "Agency",
                UUID.randomUUID().toString(),
                "AgencyRegisteredEvent",
                objectMapper.writeValueAsString(event));

        when(outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING))
                .thenReturn(List.of(outboxEvent));

        outboxScheduler.processOutboxEvents();

        verify(eventPublisher).publishEvent(any(AgencyRegisteredEvent.class));
        verify(outboxRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getStatus()).isEqualTo(OutboxEventStatus.SENT);
    }

    @Test
    void shouldMarkExpiredEventsAsFailed() throws Exception {
        var event = AgencyRegisteredEvent.of(AgencyId.generate(), "Test Agency");

        var outboxEvent = createExpiredEvent(event);

        when(outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING))
                .thenReturn(List.of(outboxEvent));

        outboxScheduler.processOutboxEvents();

        verify(eventPublisher, never()).publishEvent(any());
        verify(outboxRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getStatus()).isEqualTo(OutboxEventStatus.FAILED);
    }

    private OutboxEvent createExpiredEvent(AgencyRegisteredEvent event) throws Exception {
        var outboxEvent = new OutboxEvent(
                UUID.randomUUID(),
                "Agency",
                UUID.randomUUID().toString(),
                "AgencyRegisteredEvent",
                objectMapper.writeValueAsString(event));

        var createdAtField = OutboxEvent.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(outboxEvent, Instant.now().minus(600, ChronoUnit.SECONDS));

        return outboxEvent;
    }
}
