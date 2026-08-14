package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.CityJpa;
import cm.jemil.agency.domain.city.views.CityView.CityView1;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CitySpringRepository extends JpaRepository<CityJpa, UUID> {

    @Query(value = """
            SELECT
                c.id                              AS id,
                c.name                            AS name,
                c.region                          AS region,
                c.createdAt                       AS createdAt
            FROM CityJpa c
            ORDER BY c.createdAt ASC
            """, countQuery = """
            SELECT COUNT(c)
                    FROM CityJpa c
            """)
    Page<CityView1> findAllCities(Pageable pageable);
}
