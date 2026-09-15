package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BookingJpa;
import cm.jemil.booking.domain.booking.BookingExpiryRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaBookingExpiryRepository implements BookingExpiryRepository {

    private static final String EXPIRED = "EXPIRED";

    private final BookingSpringRepository bookingSpringRepository;
    private final SeatAssignmentSpringRepository seatAssignmentSpringRepository;

    @Override
    public List<UUID> findExpiredHeldBookingIds(OffsetDateTime now) {
        return bookingSpringRepository.findExpiredHolds(now).stream()
                .map(BookingJpa::getId)
                .toList();
    }

    @Override
    public void expireHold(UUID bookingId) {
        bookingSpringRepository.findById(bookingId).ifPresent(booking -> {
            booking.setStatus(EXPIRED);
            bookingSpringRepository.save(booking);
        });
        seatAssignmentSpringRepository.releaseHeldSeatsForBooking(bookingId);
    }
}
