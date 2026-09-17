package cm.jemil.booking.domain.trip;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a bus ID.
 */
public record BusId(UUID value) {

    public BusId {
        Objects.requireNonNull(value, "Bus ID cannot be null");
    }

    public static BusId generate() {
        return new BusId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusId that = (BusId) o;
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
