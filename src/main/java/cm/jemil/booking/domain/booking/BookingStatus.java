package cm.jemil.booking.domain.booking;

/**
 * Status of a booking.
 */
public enum BookingStatus {
    HELD,
    PENDING_PAYMENT,
    PAID,
    CANCELLED,
    EXPIRED
}
