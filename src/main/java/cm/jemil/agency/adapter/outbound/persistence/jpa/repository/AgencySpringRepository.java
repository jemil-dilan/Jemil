package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.RouteJpa;
import cm.jemil.agency.domain.agency.AgencyStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
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
            LEFT JOIN CityJpa city ON city.id = branch.cityId
            WHERE (:cityName IS NULL OR lower(city.name) = lower(:cityName))
            """)
    List<AgencyJpa> findAllWithBranchesByCity(@Param("cityName") String cityName);

    @Query("""
            select distinct agency
            from AgencyJpa agency
            LEFT JOIN FETCH agency.branches branch
            """)
    List<AgencyJpa> findAllWithBranches();

    @Query("""
            SELECT DISTINCT agency
            FROM AgencyJpa agency
            LEFT JOIN FETCH agency.branches branch
            LEFT JOIN CityJpa city ON city.id = branch.cityId
            WHERE agency.id = :id
            """)
    Optional<AgencyJpa> findByIdView1(@Param("id") UUID id);

    @Query("""
            SELECT DISTINCT agency
            FROM AgencyJpa agency
            LEFT JOIN FETCH agency.branches branch
            LEFT JOIN CityJpa city ON city.id = branch.cityId
            WHERE agency.status = 'ACTIVE'
            AND (:cityName IS NULL OR lower(city.name) = lower(:cityName))
            """)
    List<AgencyJpa> findAllWithBranchesByCity(@Param("cityName") String cityName, Pageable pageable);

    @Query("""
            SELECT COUNT(DISTINCT agency.id)
            FROM AgencyJpa agency
            LEFT JOIN agency.branches branch
            LEFT JOIN CityJpa city ON city.id = branch.cityId
            WHERE agency.status = 'ACTIVE'
            AND (:cityName IS NULL OR lower(city.name) = lower(:cityName))
            """)
    long countByBranchCityName(@Param("cityName") String cityName);

    @Query("""
            SELECT DISTINCT route
            FROM AgencyJpa agency
            JOIN agency.routes route
            LEFT JOIN FETCH route.schedules schedule
            WHERE agency.status = 'ACTIVE'
            AND route.departureId = :origin
            AND route.arrivalId = :destination
            """)
    List<RouteJpa> findRoutesByCities(@Param("origin") UUID origin, @Param("destination") UUID destination);

    @Query(value = """
        SELECT DISTINCT a
        FROM AgencyJpa a
        LEFT JOIN a.branches b
        LEFT JOIN CityJpa c ON c.id = b.cityId
        LEFT JOIN a.routes r
        WHERE a.status = :status
        AND (
            :cityName IS NULL
            OR :cityName = ''
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :cityName, '%'))
        )
        """, countQuery = """
        SELECT COUNT(DISTINCT a)
        FROM AgencyJpa a
        LEFT JOIN a.branches b
        LEFT JOIN CityJpa c ON c.id = b.cityId
        WHERE (
            :cityName IS NULL
            OR :cityName = ''
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :cityName, '%'))
        )
        """)
    Page<AgencyJpa> findAllAgencies(
            @Param("cityName") String cityName, @Param("status") AgencyStatus status, Pageable pageable);
}
