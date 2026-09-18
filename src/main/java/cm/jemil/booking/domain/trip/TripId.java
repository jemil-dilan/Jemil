package cm.jemil.booking.domain.trip;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a trip ID.
 */
public record TripId(UUID value) {

    public TripId {
        Objects.requireNonNull(value, "Trip ID cannot be null");
    }

    public static TripId generate() {
        return new TripId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripId that = (TripId) o;
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
