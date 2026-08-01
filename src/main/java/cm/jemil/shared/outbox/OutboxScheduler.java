package cm.jemil.shared.outbox;

import cm.jemil.shared.events.DomainEvent;
import cm.jemil.shared.events.DomainEventType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final List<DomainEventType> domainEventTypes;

    private static final int MAX_RETRY_SECONDS = 300;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                if (event.isExpired(MAX_RETRY_SECONDS)) {
                    log.warn("Outbox event {} exceeded max retry time, marking as failed", event.getId());
                    event.markAsFailed();
                    outboxRepository.save(event);
                    continue;
                }

                log.info("Processing outbox event: {} of type {}", event.getId(), event.getEventType());
                eventPublisher.publishEvent(deserialize(event));

                event.markAsSent();
                outboxRepository.save(event);
            } catch (JacksonException | IllegalArgumentException e) {
                log.error("Discarding invalid outbox event: {}", event.getId(), e);
                event.markAsFailed();
                outboxRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to publish outbox event, leaving it pending for retry: {}", event.getId(), e);
            }
        }
    }

    private DomainEvent deserialize(OutboxEvent event) throws JacksonException {
        Class<? extends DomainEvent> eventClass = eventTypes().get(event.getEventType());
        if (eventClass == null) {
            throw new IllegalArgumentException("Unsupported outbox event type: " + event.getEventType());
        }
        return objectMapper.readValue(event.getPayload(), eventClass);
    }

    private Map<String, Class<? extends DomainEvent>> eventTypes() {
        Map<String, Class<? extends DomainEvent>> eventTypes = new HashMap<>();
        for (DomainEventType eventType : domainEventTypes) {
            eventTypes.put(eventType.name(), eventType.eventClass());
        }
        return eventTypes;
    }
}
