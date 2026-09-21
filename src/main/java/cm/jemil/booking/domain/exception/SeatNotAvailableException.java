package cm.jemil.booking.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class SeatNotAvailableException extends DomainException {
    public SeatNotAvailableException() {
        super(BookingErrorCode.BOOKING_409_001);
    }
}
