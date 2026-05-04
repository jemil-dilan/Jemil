package cm.jemil.agency.infrastructure.messaging;

import cm.jemil.agency.domain.port.out.AgencyEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adapter sortant : publie les événements domaine via Spring.
 *
 * <p>En phase monolithe modulaire, on utilise Spring ApplicationEventPublisher.
 * Les événements sont synchrones et dans la même JVM.
 *
 * <p>Quand on extrait en microservices, on remplace cette classe
 * par une implémentation RabbitMQ. Le domaine et l'application
 * ne changent ABSOLUMENT PAS — seul cet adapter change.
 *
 * <p>C'est toute la puissance de l'architecture hexagonale.
 */
@Component
public class SpringAgencyEventPublisher implements AgencyEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SpringAgencyEventPublisher.class);

    private final ApplicationEventPublisher springPublisher;

    public SpringAgencyEventPublisher(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publish(Object domainEvent) {
        log.debug("Publication de l'événement domaine : {}", domainEvent.getClass().getSimpleName());
        springPublisher.publishEvent(domainEvent);
    }
}
