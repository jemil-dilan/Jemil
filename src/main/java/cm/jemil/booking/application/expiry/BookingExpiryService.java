package cm.jemil.booking.application.expiry;

import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.BookingSpringRepository;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.SeatAssignmentSpringRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UC-S-01 — releases expired seat holds.
 * Payment-aware skip (INITIATED/PENDING) is deferred until the payment module exists.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingExpiryService {

    private static final String HELD = "HELD";
    private static final String EXPIRED = "EXPIRED";

    private final BookingSpringRepository bookingSpringRepository;
    private final SeatAssignmentSpringRepository seatAssignmentSpringRepository;
    private final Clock clock;

    @Transactional
    public int expireHolds() {
        var now = OffsetDateTime.now(clock.withZone(ZoneOffset.UTC));
        var expired = bookingSpringRepository.findExpiredHolds(now);
        int released = 0;

        for (var booking : expired) {
            if (!HELD.equals(booking.getStatus())) {
                continue;
            }
            booking.setStatus(EXPIRED);
            bookingSpringRepository.save(booking);
            seatAssignmentSpringRepository.releaseHeldSeatsForBooking(booking.getId());
            released++;
        }

        if (released > 0) {
            log.info("UC-S-01 booking expiry released {} expired hold(s)", released);
        } else {
            log.debug("UC-S-01 booking expiry found no expired holds at {}", now);
        }
        return released;
    }
}
