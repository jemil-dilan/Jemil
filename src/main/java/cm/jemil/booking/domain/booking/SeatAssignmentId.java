package cm.jemil.booking.domain.booking;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a seat assignment ID.
 */
public record SeatAssignmentId(UUID value) {

    public SeatAssignmentId {
        Objects.requireNonNull(value, "Seat assignment ID cannot be null");
    }

    public static SeatAssignmentId generate() {
        return new SeatAssignmentId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatAssignmentId that = (SeatAssignmentId) o;
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
