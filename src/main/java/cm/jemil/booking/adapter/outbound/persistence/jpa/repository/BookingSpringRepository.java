package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BookingJpa;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingSpringRepository extends JpaRepository<BookingJpa, UUID> {

    Optional<BookingJpa> findByRef(String ref);

    List<BookingJpa> findByTripId(UUID tripId);

    List<BookingJpa> findByStatusAndHoldExpiresAtBefore(String status, OffsetDateTime now);

    @Query("""
            SELECT b FROM BookingJpa b
            WHERE b.status = 'HELD'
              AND b.holdExpiresAt IS NOT NULL
              AND b.holdExpiresAt < :now
            """)
    List<BookingJpa> findExpiredHolds(@Param("now") OffsetDateTime now);

    @Query("""
            SELECT COUNT(sa) > 0 FROM SeatAssignmentJpa sa
            JOIN BookingJpa b ON sa.bookingId = b.id
            WHERE b.tripId = :tripId
              AND sa.seatNo = :seatNo
              AND b.status IN :statuses
            """)
    boolean existsByTripIdAndSeatNoAndStatusIn(
            @Param("tripId") UUID tripId, @Param("seatNo") int seatNo, @Param("statuses") List<String> statuses);
}
