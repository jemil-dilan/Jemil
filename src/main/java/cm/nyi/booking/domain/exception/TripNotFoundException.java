package cm.nyi.booking.domain.exception;

import cm.nyi.shared.exception.DomainException;

public class TripNotFoundException extends DomainException {
    public TripNotFoundException() {
        super(BookingErrorCode.BOOKING_404_001);
    }
}
