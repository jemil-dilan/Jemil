package cm.jemil.booking.domain.booking;

import cm.jemil.booking.domain.trip.TripId;
import cm.jemil.shared.utils.CreatedAt;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Domain entity representing a booking.
 */
@Getter
@Setter
public class Booking {

    private final BookingId id;
    private final BookingReference ref;
    private final TripId tripId;
    private final Channel channel;
    private final PassengerInfo passengerInfo;
    private final int amountXaf;
    private BookingStatus status;
    private OffsetDateTime holdExpiresAt;
    private final CreatedAt createdAt;

    private Booking(
            BookingId id,
            BookingReference ref,
            TripId tripId,
            Channel channel,
            PassengerInfo passengerInfo,
            int amountXaf,
            BookingStatus status,
            OffsetDateTime holdExpiresAt,
            CreatedAt createdAt) {
        this.id = Objects.requireNonNull(id, "Booking ID cannot be null");
        this.ref = Objects.requireNonNull(ref, "Booking reference cannot be null");
        this.tripId = Objects.requireNonNull(tripId, "Trip ID cannot be null");
        this.channel = Objects.requireNonNull(channel, "Channel cannot be null");
        this.passengerInfo = Objects.requireNonNull(passengerInfo, "Passenger info cannot be null");
        this.amountXaf = amountXaf;
        this.status = Objects.requireNonNull(status, "Booking status cannot be null");
        this.holdExpiresAt = holdExpiresAt;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
    }

    public static Booking hold(
            BookingId id,
            TripId tripId,
            Channel channel,
            PassengerInfo passengerInfo,
            int amountXaf,
            OffsetDateTime holdExpiresAt,
            CreatedAt createdAt) {
        return new Booking(
                id,
                BookingReference.generate(id.value()),
                tripId,
                channel,
                passengerInfo,
                amountXaf,
                BookingStatus.HELD,
                holdExpiresAt,
                createdAt);
    }

    /**
     * Reconstitute a booking from persistence.
     */
    public static Booking reconstitute(
            BookingId id,
            BookingReference ref,
            TripId tripId,
            Channel channel,
            PassengerInfo passengerInfo,
            int amountXaf,
            BookingStatus status,
            OffsetDateTime holdExpiresAt,
            CreatedAt createdAt) {
        return new Booking(id, ref, tripId, channel, passengerInfo, amountXaf, status, holdExpiresAt, createdAt);
    }

    public boolean isExpired(OffsetDateTime now) {
        return holdExpiresAt != null && now.isAfter(holdExpiresAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
