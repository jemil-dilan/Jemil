package cm.jemil.booking.domain.booking;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a booking ID.
 */
public record BookingId(UUID value) {

    public BookingId {
        Objects.requireNonNull(value, "Booking ID cannot be null");
    }

    public static BookingId generate() {
        return new BookingId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingId that = (BookingId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
