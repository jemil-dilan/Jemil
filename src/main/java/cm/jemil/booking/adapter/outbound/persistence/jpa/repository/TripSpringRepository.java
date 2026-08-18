package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.TripJpa;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripSpringRepository extends JpaRepository<TripJpa, UUID> {}
