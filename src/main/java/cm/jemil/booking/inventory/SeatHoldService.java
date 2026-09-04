package cm.jemil.booking.inventory;

import cm.jemil.booking.domain.exception.BookingErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private final SeatHoldExecutor seatHoldExecutor;

    public BookingHoldResult placeHold(
            UUID tripId, List<Integer> seatNos, String passengerName, String passengerMsisdn, int amountXaf) {
        try {
            return seatHoldExecutor.placeHold(tripId, seatNos, passengerName, passengerMsisdn, amountXaf);
        } catch (SeatHoldExecutor.SeatUnavailableException ex) {
            throw new DomainException(BookingErrorCode.BOOKING_409_001);
        }
    }

    public HoldOutcome tryHoldSeat(UUID tripId, int seatNo, String passengerName) {
        try {
            placeHold(tripId, List.of(seatNo), passengerName, null, 0);
            return HoldOutcome.HELD;
        } catch (DomainException ex) {
            if (BookingErrorCode.BOOKING_409_001.getCode().equals(ex.getCode())) {
                return HoldOutcome.SEAT_UNAVAILABLE;
            }
            throw ex;
        }
    }
}
