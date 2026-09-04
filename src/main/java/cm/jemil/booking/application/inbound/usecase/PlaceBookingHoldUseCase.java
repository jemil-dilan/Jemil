package cm.jemil.booking.application.inbound.usecase;

import cm.jemil.booking.inventory.BookingHoldResult;
import java.util.UUID;

public interface PlaceBookingHoldUseCase {
    BookingHoldResult execute(Command command);

    record Command(UUID tripId, java.util.List<Integer> seatNos, String passengerName, String passengerMsisdn) {}
}
