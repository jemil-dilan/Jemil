package cm.jemil.booking.inventory;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private final SeatHoldExecutor seatHoldExecutor;

    public HoldOutcome tryHoldSeat(UUID tripId, int seatNo, String passengerName) {
        try {
            return seatHoldExecutor.hold(tripId, seatNo, passengerName);
        } catch (SeatHoldExecutor.SeatUnavailableException ex) {
            return HoldOutcome.SEAT_UNAVAILABLE;
        }
    }
}
