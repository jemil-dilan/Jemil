package cm.jemil.booking.domain.seat;

import java.util.Objects;

/**
 * Value object representing a seat number on a bus.
 */
public record SeatNumber(int value) {

    public SeatNumber {
        if (value < 1) {
            throw new IllegalArgumentException("Seat number must be at least 1");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatNumber that = (SeatNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
