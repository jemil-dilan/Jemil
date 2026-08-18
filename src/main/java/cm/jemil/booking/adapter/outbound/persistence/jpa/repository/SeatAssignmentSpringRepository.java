package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.SeatAssignmentJpa;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatAssignmentSpringRepository extends JpaRepository<SeatAssignmentJpa, UUID> {

    long countByTripIdAndSeatNoAndStatusIn(UUID tripId, int seatNo, Iterable<String> statuses);
}
