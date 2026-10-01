package cm.nyi.booking.domain.trip;

import cm.nyi.booking.domain.exception.BookingErrorCode;
import cm.nyi.shared.exception.DomainException;

public class CityDuplicationException extends DomainException {

    public CityDuplicationException() {
        super(BookingErrorCode.BOOKING_400_001);
    }
}
