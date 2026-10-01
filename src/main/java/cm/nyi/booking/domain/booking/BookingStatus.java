package cm.nyi.booking.domain.booking;

/**
 * Status of a booking.
 */
public enum BookingStatus {
    HELD,
    PENDING_PAYMENT,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}
