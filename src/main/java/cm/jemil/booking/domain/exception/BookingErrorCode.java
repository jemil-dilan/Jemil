package cm.jemil.booking.domain.exception;

import cm.jemil.shared.exception.ErrorCode;

public enum BookingErrorCode implements ErrorCode {
    BOOKING_400_001("BOOKING_400_001", "Origin and destination cannot be the same"),
    BOOKING_400_002("BOOKING_400_002", "Service date cannot be in the past"),
    BOOKING_400_003("BOOKING_400_003", "At least one seat is required"),
    BOOKING_400_004("BOOKING_400_004", "A booking cannot hold more than 5 seats"),
    BOOKING_400_005("BOOKING_400_005", "Invalid seat number for this trip"),
    BOOKING_400_006("BOOKING_400_006", "Passenger name is required"),
    BOOKING_404_001("BOOKING_404_001", "Trip not found"),
    BOOKING_409_001("BOOKING_409_001", "One or more seats are no longer available");

    private final String code;
    private final String message;

    BookingErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
