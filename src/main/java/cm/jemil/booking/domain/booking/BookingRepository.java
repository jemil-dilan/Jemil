package cm.jemil.booking.domain.booking;

import cm.jemil.booking.domain.seat.SeatNumber;
import cm.jemil.booking.domain.trip.TripId;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for booking domain operations.
 */
public interface BookingRepository {

    /**
     * Save a booking.
     */
    Booking save(Booking booking);

    /**
     * Find a booking by ID.
     */
    Optional<Booking> findById(BookingId id);

    /**
     * Find a booking by reference.
     */
    Optional<Booking> findByReference(BookingReference ref);

    /**
     * Find all bookings for a trip.
     */
    List<Booking> findByTripId(TripId tripId);

    /**
     * Check if a seat is available (not HELD or SOLD) for a trip.
     */
    boolean isSeatAvailable(TripId tripId, SeatNumber seatNo);

    /**
     * Find expired bookings (hold expired and still in HELD status).
     */
    List<Booking> findExpiredBookings();
}
