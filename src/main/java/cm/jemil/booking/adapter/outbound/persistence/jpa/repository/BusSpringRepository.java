package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BusJpa;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusSpringRepository extends JpaRepository<BusJpa, UUID> {}
