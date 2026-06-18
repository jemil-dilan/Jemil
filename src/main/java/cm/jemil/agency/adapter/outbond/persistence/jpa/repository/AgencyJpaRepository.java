package cm.jemil.agency.adapter.outbond.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbond.persistence.jpa.entity.AgencyJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyJpaRepository extends JpaRepository<AgencyJpaEntity, UUID> {}
