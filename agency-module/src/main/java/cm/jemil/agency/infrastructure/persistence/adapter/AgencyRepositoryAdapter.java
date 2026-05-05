package cm.jemil.agency.infrastructure.persistence.adapter;

import cm.jemil.agency.domain.model.*;
import cm.jemil.agency.domain.port.out.AgencyRepository;
import cm.jemil.agency.infrastructure.persistence.entity.AgencyJpaEntity;
import cm.jemil.agency.infrastructure.persistence.entity.RouteJpaEntity;
import cm.jemil.agency.infrastructure.persistence.repository.AgencyJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Adapter sortant : implémente le port AgencyRepository du domaine.
 *
 * <p>C'est le PONT entre le domaine (Aggregates) et la base de données (JPA Entities).
 * Responsabilités :
 * - Convertir un Aggregate domaine → JPA Entity (pour sauvegarder)
 * - Convertir une JPA Entity → Aggregate domaine (pour lire)
 *
 * <p>Le domaine appelle AgencyRepository (l'interface).
 * Spring injecte AgencyRepositoryAdapter (cette implémentation).
 * Le domaine ne sait pas que cet adapter existe.
 */
@Component
public class AgencyRepositoryAdapter implements AgencyRepository {

    private final AgencyJpaRepository jpaRepository;

    public AgencyRepositoryAdapter(AgencyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Agency save(Agency agency) {
        AgencyJpaEntity entity = toJpaEntity(agency);
        AgencyJpaEntity saved = jpaRepository.save(entity);
        return toDomainAgency(saved);
    }

    @Override
    public Optional<Agency> findById(AgencyId agencyId) {
        return jpaRepository.findById(agencyId.value()).map(this::toDomainAgency);
    }

    @Override
    public List<Agency> findAllActive() {
        return jpaRepository.findByStatus(AgencyStatus.ACTIVE).stream()
                .map(this::toDomainAgency)
                .toList();
    }

    @Override
    public List<Agency> findByCity(String city) {
        return jpaRepository.findActiveByCityIgnoreCase(city).stream()
                .map(this::toDomainAgency)
                .toList();
    }

    @Override
    public boolean existsById(AgencyId agencyId) {
        return jpaRepository.existsById(agencyId.value());
    }

    // ── Conversion Domaine → JPA ───────────────────────────────
    private AgencyJpaEntity toJpaEntity(Agency agency) {
        AgencyJpaEntity entity = new AgencyJpaEntity(
                agency.getId().value(),
                agency.getName(),
                agency.getCity(),
                agency.getContactPhone(),
                agency.getStatus());

        // Conversion des routes
        agency.getRoutes().forEach(route -> {
            RouteJpaEntity routeEntity = new RouteJpaEntity(
                    route.getId().value(), entity, route.getOrigin(), route.getDestination(), route.getTotalSeats());
            entity.getRoutes().add(routeEntity);
        });

        return entity;
    }

    // ── Conversion JPA → Domaine ───────────────────────────────
    private Agency toDomainAgency(AgencyJpaEntity entity) {
        List<Route> routes =
                entity.getRoutes().stream().map(this::toDomainRoute).toList();

        return Agency.reconstitute(
                AgencyId.from(entity.getId()),
                entity.getName(),
                entity.getCity(),
                entity.getContactPhone(),
                entity.getStatus(),
                routes);
    }

    private Route toDomainRoute(RouteJpaEntity entity) {
        return Route.reconstitute(
                RouteId.from(entity.getId()),
                AgencyId.from(entity.getAgency().getId()),
                entity.getOrigin(),
                entity.getDestination(),
                entity.getTotalSeats(),
                entity.isActive());
    }
}
