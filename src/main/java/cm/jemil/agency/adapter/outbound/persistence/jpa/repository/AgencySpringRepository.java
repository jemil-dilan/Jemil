package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyJpa;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencySpringRepository extends JpaRepository<AgencyJpa, UUID> {

    @Query("""
            SELECT DISTINCT agency
            FROM AgencyJpa agency
            LEFT JOIN FETCH agency.branches branch
            LEFT JOIN FETCH branch.city city
            WHERE (:cityName IS NULL OR lower(city.name) = lower(:cityName))
            """)
    List<AgencyJpa> findAllWithBranchesByCity(@Param("cityName") String cityName);

    @Query("""
            select distinct agency
            from AgencyJpa agency
            LEFT JOIN FETCH agency.branches branch
            LEFT JOIN FETCH branch.city
            """)
    List<AgencyJpa> findAllWithBranches();

    @Query("""
            SELECT DISTINCT agency
            FROM AgencyJpa agency
            LEFT JOIN FETCH agency.branches branch
            LEFT JOIN FETCH branch.city
            WHERE agency.id = :id
            """)
    Optional<AgencyJpa> findByIdView1(@Param("id") UUID id);

    @Query("""
            SELECT DISTINCT agency
            FROM AgencyJpa agency
            LEFT JOIN FETCH agency.branches branch
            LEFT JOIN FETCH branch.city city
            WHERE agency.status = 'ACTIVE'
            AND (:cityName IS NULL OR lower(city.name) = lower(:cityName))
            """)
    List<AgencyJpa> findAllWithBranchesByCity(@Param("cityName") String cityName, Pageable pageable);

    @Query("""
            SELECT COUNT(DISTINCT agency.id)
            FROM AgencyJpa agency
            LEFT JOIN agency.branches branch
            LEFT JOIN branch.city city
            WHERE agency.status = 'ACTIVE'
            AND (:cityName IS NULL OR lower(city.name) = lower(:cityName))
            """)
    long countByBranchCityName(@Param("cityName") String cityName);
}
