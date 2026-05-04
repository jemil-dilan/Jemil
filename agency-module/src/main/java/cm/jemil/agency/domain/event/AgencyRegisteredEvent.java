package cm.jemil.agency.domain.event;

import cm.jemil.agency.domain.model.AgencyId;
import java.time.Instant;

/**
 * Événement domaine : une nouvelle agence a été enregistrée.
 *
 * <p>Les événements domaine sont immuables (record) et décrivent
 * quelque chose qui S'EST PASSÉ (passé composé toujours).
 * Ils sont collectés dans l'Aggregate et publiés après la persistance.
 *
 * <p>Quand on passe en microservices, cet événement sera sérialisé
 * en JSON et publié sur RabbitMQ / Kafka.
 */
public record AgencyRegisteredEvent(
        AgencyId agencyId,
        String agencyName,
        String city,
        Instant occurredAt) {

    public AgencyRegisteredEvent(AgencyId agencyId, String agencyName, String city) {
        this(agencyId, agencyName, city, Instant.now());
    }
}
