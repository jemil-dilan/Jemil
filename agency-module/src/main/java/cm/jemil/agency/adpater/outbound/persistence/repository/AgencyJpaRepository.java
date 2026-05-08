package cm.jemil.agency.adpater.outbound.persistence.repository;

import cm.jemil.agency.domain.model.AgencyStatus;
import cm.jemil.agency.adpater.outbound.persistence.entity.AgencyJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository Spring Data JPA.
 *
 * <p>Cette interface est dans l'INFRASTRUCTURE — le domaine ne la voit pas.
 * Elle parle JPA, UUID, entities. Le domaine parle Aggregates, Value Objects.
 * L'adapter (AgencyRepositoryAdapter) fait la traduction entre les deux.
 */
public interface AgencyJpaRepository extends JpaRepository<AgencyJpaEntity, UUID> {

    List<AgencyJpaEntity> findByStatus(AgencyStatus status);

    @Query("SELECT a FROM AgencyJpaEntity a WHERE LOWER(a.city) = LOWER(:city) AND a.status = 'ACTIVE'")
    List<AgencyJpaEntity> findActiveByCityIgnoreCase(@Param("city") String city);

    boolean existsByNameIgnoreCaseAndCityIgnoreCase(String name, String city);
}
