package cm.jemil.shared.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                // In Phase 1, we just publish locally via Spring ApplicationEvents
                // We'll need to deserialize the payload if we want to publish the specific event class
                // For now, let's assume we might need the class type in the OutboxEvent

                // Simplified for now: just log and mark as sent
                log.info("Processing outbox event: {} of type {}", event.getId(), event.getEventType());

                // In a real scenario, we'd deserialize event.payload to its class and publish it
                // eventPublisher.publishEvent(deserializedEvent);

                event.markAsSent();
                outboxRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to process outbox event: {}", event.getId(), e);
                event.markAsFailed();
                outboxRepository.save(event);
            }
        }
    }
}
