package cm.nyi.booking.domain.exception;

import cm.nyi.shared.exception.DomainException;

public class SeatNotAvailableException extends DomainException {
    public SeatNotAvailableException() {
        super(BookingErrorCode.BOOKING_409_001);
    }
}
