package cm.jemil.shared.outbox;

import cm.jemil.shared.events.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventPublisherImpl implements OutboxEventPublisher {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void publish(DomainEvent domainEvent) {
        try {
            OutboxEvent event = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(domainEvent.getClass().getSimpleName()) // Basic implementation
                    .aggregateId(UUID.randomUUID()) // Should ideally come from domainEvent
                    .eventType(domainEvent.getClass().getName())
                    .payload(objectMapper.writeValueAsString(domainEvent))
                    .status(OutboxEventStatus.PENDING)
                    .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                    .build();

            outboxRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize domain event", e);
        }
    }
}
