package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BusJpa;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusSpringRepository extends JpaRepository<BusJpa, UUID> {
    List<BusJpa> findByAgencyId(UUID agencyId);
}
