package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BookingJpa;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingSpringRepository extends JpaRepository<BookingJpa, UUID> {

    @Query("""
            SELECT b FROM BookingJpa b
            WHERE b.status = 'HELD'
              AND b.holdExpiresAt IS NOT NULL
              AND b.holdExpiresAt < :now
            """)
    List<BookingJpa> findExpiredHolds(@Param("now") OffsetDateTime now);
}
