package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyBranchJpa;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyBranchSpringRepository extends JpaRepository<AgencyBranchJpa, UUID> {

    @Query("select b from AgencyBranchJpa b where b.agencyId = :agencyId")
    List<AgencyBranchJpa> findAllByAgencyIdWithCity(@Param("agencyId") UUID agencyId);
}
