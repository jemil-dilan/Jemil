package cm.jemil.booking.domain.booking;

/**
 * Repository interface for booking domain operations.
 */
public interface BookingRepository {

    /**
     * Save a booking.
     */
    void save(Booking booking);
}
