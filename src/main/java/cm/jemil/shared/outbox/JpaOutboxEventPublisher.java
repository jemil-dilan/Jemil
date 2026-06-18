package cm.jemil.shared.outbox;

import cm.jemil.shared.events.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JpaOutboxEventPublisher implements OutboxEventPublisher {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    @SneakyThrows
    public void publish(Object event) {
        if (!(event instanceof DomainEvent domainEvent)) {
            return;
        }

        String payload = objectMapper.writeValueAsString(domainEvent);

        OutboxEvent outboxEvent = new OutboxEvent(
                domainEvent.eventId(),
                "UNKNOWN", // This should be passed or inferred
                "UNKNOWN", // This should be passed or inferred
                domainEvent.getClass().getSimpleName(),
                payload);

        outboxRepository.save(outboxEvent);
    }
}
