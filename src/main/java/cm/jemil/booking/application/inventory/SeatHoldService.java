package cm.jemil.booking.application.inventory;

import cm.jemil.booking.domain.exception.BookingErrorCode;
import cm.jemil.booking.domain.exception.SeatNotAvailableException;
import cm.jemil.shared.exception.DomainException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private final SeatHoldExecutor seatHoldExecutor;

    public UUID placeHold(
            UUID tripId, List<Integer> seatNos, String passengerName, String passengerMsisdn, int amountXaf) {
        try {
            return seatHoldExecutor.placeHold(tripId, seatNos, passengerName, passengerMsisdn, amountXaf);
        } catch (SeatHoldExecutor.SeatUnavailableException _) {
            throw new SeatNotAvailableException();
        }
    }

    public HoldOutcome tryHoldSeat(UUID tripId, int seatNo, String passengerName) {
        try {
            placeHold(tripId, List.of(seatNo), passengerName, null, 0);
            return HoldOutcome.HELD;
        } catch (DataIntegrityViolationException ex) {
            // Check if this is a seat conflict
            if (SeatHoldExecutor.isSeatConflict(ex)) {
                return HoldOutcome.SEAT_UNAVAILABLE;
            }
            throw ex;
        } catch (SeatHoldExecutor.SeatUnavailableException _) {
            return HoldOutcome.SEAT_UNAVAILABLE;
        } catch (DomainException ex) {
            if (BookingErrorCode.BOOKING_409_001.getCode().equals(ex.getCode())) {
                return HoldOutcome.SEAT_UNAVAILABLE;
            }
            throw ex;
        }
    }
}
