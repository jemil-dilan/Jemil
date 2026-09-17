package cm.jemil.booking.domain.trip;

import cm.jemil.booking.domain.exception.BookingErrorCode;
import cm.jemil.shared.exception.DomainException;

public class TripNotFoundException extends DomainException {

    public TripNotFoundException() {
        super(BookingErrorCode.BOOKING_404_001);
    }
}
