package cm.jemil.booking.domain.booking;

import cm.jemil.booking.domain.seat.SeatNumber;
import cm.jemil.booking.domain.seat.SeatStatus;
import cm.jemil.booking.domain.trip.TripId;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for seat assignment domain operations.
 */
public interface SeatAssignmentRepository {

    /**
     * Save a seat assignment.
     */
    SeatAssignment save(SeatAssignment seatAssignment);

    /**
     * Save multiple seat assignments.
     */
    List<SeatAssignment> saveAll(List<SeatAssignment> seatAssignments);

    /**
     * Find a seat assignment by ID.
     */
    Optional<SeatAssignment> findById(SeatAssignmentId id);

    /**
     * Find seat assignments by booking ID.
     */
    List<SeatAssignment> findByBookingId(BookingId bookingId);

    /**
     * Find seat assignments by trip ID.
     */
    List<SeatAssignment> findByTripId(TripId tripId);

    /**
     * Find seat assignment by trip and seat number.
     */
    Optional<SeatAssignment> findByTripIdAndSeatNo(TripId tripId, SeatNumber seatNo);

    /**
     * Find all seat numbers that are taken (HELD or SOLD) for a trip.
     */
    List<Integer> findTakenSeatNosByTripId(TripId tripId);

    /**
     * Check if a seat is available for a trip.
     */
    boolean isSeatAvailable(TripId tripId, SeatNumber seatNo);

    /**
     * Find seat assignments by status for a trip.
     */
    List<SeatAssignment> findByTripIdAndStatus(TripId tripId, SeatStatus status);
}
