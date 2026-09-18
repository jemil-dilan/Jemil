package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.SeatAssignmentJpa;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatAssignmentSpringRepository extends JpaRepository<SeatAssignmentJpa, UUID> {

    List<SeatAssignmentJpa> findByBookingId(UUID bookingId);

    List<SeatAssignmentJpa> findByTripId(UUID tripId);

    Optional<SeatAssignmentJpa> findByTripIdAndSeatNo(UUID tripId, int seatNo);

    List<SeatAssignmentJpa> findByTripIdAndStatus(UUID tripId, String status);

    long countByTripIdAndSeatNoAndStatusIn(UUID tripId, int seatNo, Iterable<String> statuses);

    @Query("""
            SELECT s.seatNo FROM SeatAssignmentJpa s
            WHERE s.tripId = :tripId AND s.status IN ('HELD', 'SOLD')
            ORDER BY s.seatNo ASC
            """)
    List<Integer> findTakenSeatNosByTripId(@Param("tripId") UUID tripId);

    @Query("""
            SELECT COUNT(s) > 0 FROM SeatAssignmentJpa s
            WHERE s.tripId = :tripId AND s.seatNo = :seatNo AND s.status IN :statuses
            """)
    boolean existsByTripIdAndSeatNoAndStatusIn(
            @Param("tripId") UUID tripId, @Param("seatNo") int seatNo, @Param("statuses") List<String> statuses);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE SeatAssignmentJpa s
            SET s.status = 'RELEASED'
            WHERE s.bookingId = :bookingId AND s.status = 'HELD'
            """)
    int releaseHeldSeatsForBooking(@Param("bookingId") UUID bookingId);
}
