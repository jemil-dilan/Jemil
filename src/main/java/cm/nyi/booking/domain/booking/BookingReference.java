package cm.nyi.booking.domain.booking;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a booking reference.
 * Format: NYI-{8 uppercase characters from UUID}
 */
public record BookingReference(String value) {

    public BookingReference {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Booking reference cannot be null or blank");
        }
        // Basic validation - should start with NYI-
        if (!value.startsWith("NYI-")) {
            throw new IllegalArgumentException("Booking reference must start with NYI-");
        }
    }

    public static BookingReference generate(UUID bookingId) {
        String shortId = bookingId.toString().substring(0, 8).toUpperCase();
        return new BookingReference("NYI-" + shortId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingReference that = (BookingReference) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
