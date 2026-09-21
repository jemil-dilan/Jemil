package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.SeatAssignmentJpa;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatAssignmentSpringRepository extends JpaRepository<SeatAssignmentJpa, UUID> {

    long countByTripIdAndSeatNoAndStatusIn(UUID tripId, int seatNo, Iterable<String> statuses);

    @Query("""
            SELECT s.seatNo FROM SeatAssignmentJpa s
            WHERE s.tripId = :tripId AND s.status IN ('HELD', 'SOLD')
            ORDER BY s.seatNo ASC
            """)
    List<Integer> findTakenSeatNosByTripId(@Param("tripId") UUID tripId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE SeatAssignmentJpa s
            SET s.status = 'RELEASED'
            WHERE s.bookingId = :bookingId AND s.status = 'HELD'
            """)
    int releaseHeldSeatsForBooking(@Param("bookingId") UUID bookingId);
}
