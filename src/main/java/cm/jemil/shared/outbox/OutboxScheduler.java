package cm.jemil.shared.outbox;

import java.time.LocalDateTime;
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

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                log.info("Processing outbox event: {} of type {}", event.getId(), event.getEventType());

                // In phase 1, we just republish as Spring ApplicationEvent
                // For now, we publish the OutboxEvent itself.
                // In a more complete implementation, we would deserialize the payload.
                eventPublisher.publishEvent(event);

                event.setStatus(OutboxEventStatus.SENT);
                event.setProcessedAt(LocalDateTime.now(java.time.ZoneOffset.UTC));
                outboxRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to process outbox event: {}", event.getId(), e);
                event.setStatus(OutboxEventStatus.FAILED);
                event.setErrorMessage(e.getMessage());
                outboxRepository.save(event);
            }
        }
    }
}
