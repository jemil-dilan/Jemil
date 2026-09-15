package cm.jemil.booking.domain.booking;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Persistence port for UC-S-01 hold expiry.
 */
public interface BookingExpiryRepository {

    /** Ids of bookings still {@code HELD} whose hold TTL is strictly before {@code now}. */
    List<UUID> findExpiredHeldBookingIds(OffsetDateTime now);

    /** Marks the booking {@code EXPIRED} and releases its {@code HELD} seat assignments. */
    void expireHold(UUID bookingId);
}
