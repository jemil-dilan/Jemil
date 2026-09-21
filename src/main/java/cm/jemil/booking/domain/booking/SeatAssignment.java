package cm.jemil.booking.domain.booking;

import cm.jemil.booking.domain.seat.SeatNumber;
import cm.jemil.booking.domain.seat.SeatStatus;
import cm.jemil.booking.domain.trip.TripId;
import cm.jemil.shared.utils.CreatedAt;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Domain entity representing a seat assignment for a booking.
 */
@Getter
@Setter
public class SeatAssignment {

    private final SeatAssignmentId id;
    private final TripId tripId;
    private final SeatNumber seatNo;
    private final BookingId bookingId;
    private SeatStatus status;
    private final CreatedAt createdAt;

    private SeatAssignment(
            SeatAssignmentId id,
            TripId tripId,
            SeatNumber seatNo,
            BookingId bookingId,
            SeatStatus status,
            CreatedAt createdAt) {
        this.id = Objects.requireNonNull(id, "Seat assignment ID cannot be null");
        this.tripId = Objects.requireNonNull(tripId, "Trip ID cannot be null");
        this.seatNo = Objects.requireNonNull(seatNo, "Seat number cannot be null");
        this.bookingId = Objects.requireNonNull(bookingId, "Booking ID cannot be null");
        this.status = Objects.requireNonNull(status, "Seat status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
    }

    public static SeatAssignment create(
            TripId tripId, SeatNumber seatNo, BookingId bookingId, SeatStatus status, CreatedAt createdAt) {
        return new SeatAssignment(SeatAssignmentId.generate(), tripId, seatNo, bookingId, status, createdAt);
    }

    public static SeatAssignment reconstitute(
            SeatAssignmentId id,
            TripId tripId,
            SeatNumber seatNo,
            BookingId bookingId,
            SeatStatus status,
            CreatedAt createdAt) {
        return new SeatAssignment(id, tripId, seatNo, bookingId, status, createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatAssignment that = (SeatAssignment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
