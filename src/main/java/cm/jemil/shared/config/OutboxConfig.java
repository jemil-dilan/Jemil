package cm.jemil.shared.config;

import cm.jemil.shared.events.DomainEventType;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OutboxConfig {

    @Bean
    public List<DomainEventType> domainEventTypes(List<DomainEventType> allDomainEventTypes) {
        // Collect all DomainEventType beans defined in various modules
        return allDomainEventTypes;
    }
}
