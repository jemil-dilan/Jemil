package cm.jemil.agency.domain.port.out;

/**
 * Port sortant : publication des événements domaine.
 *
 * <p>Le domaine ne sait pas si les événements partent vers RabbitMQ,
 * Kafka, ou Spring ApplicationEventPublisher.
 * L'implémentation dans l'infrastructure décide du mécanisme.
 *
 * <p>Pendant la phase monolithe modulaire → Spring ApplicationEventPublisher.
 * Lors de l'extraction en microservices → RabbitMQ publisher.
 * Le domaine et l'application NE CHANGENT PAS.
 */
public interface AgencyEventPublisher {

    void publish(Object domainEvent);
}
