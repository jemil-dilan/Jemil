package cm.jemil.booking.application.inbound.usecase;

import java.util.List;
import java.util.UUID;

public interface PlaceBookingHoldUseCase {
    UUID execute(Command command);

    record Command(UUID tripId, List<Integer> seatNos, String passengerName, String passengerMsisdn) {}
}
