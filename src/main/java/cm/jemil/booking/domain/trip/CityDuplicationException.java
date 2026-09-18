package cm.jemil.booking.domain.trip;

import cm.jemil.booking.domain.exception.BookingErrorCode;
import cm.jemil.shared.exception.DomainException;

public class CityDuplicationException extends DomainException {

    public CityDuplicationException() {
        super(BookingErrorCode.BOOKING_400_001);
    }
}
