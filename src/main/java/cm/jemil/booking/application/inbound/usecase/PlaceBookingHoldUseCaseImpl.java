package cm.jemil.booking.application.inbound.usecase;

import cm.jemil.booking.application.inventory.BookingHoldResult;
import cm.jemil.booking.application.inventory.SeatHoldExecutor;
import cm.jemil.booking.application.inventory.SeatHoldService;
import cm.jemil.booking.domain.exception.BookingErrorCode;
import cm.jemil.booking.domain.exception.TripNotFoundException;
import cm.jemil.booking.domain.trip.TripRepository;
import cm.jemil.booking.domain.trip.TripStatus;
import cm.jemil.shared.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

@RequiredArgsConstructor
public class PlaceBookingHoldUseCaseImpl implements PlaceBookingHoldUseCase {

    private final TripRepository tripRepository;
    private final SeatHoldService seatHoldService;

    @Override
    public BookingHoldResult execute(Command command) {
        if (command.passengerName() == null || command.passengerName().isBlank()) {
            throw new DomainException(BookingErrorCode.BOOKING_400_006);
        }
        if (command.seatNos() == null || command.seatNos().isEmpty()) {
            throw new DomainException(BookingErrorCode.BOOKING_400_003);
        }
        if (command.seatNos().size() > 5) {
            throw new DomainException(BookingErrorCode.BOOKING_400_004);
        }

        var trip = tripRepository.findById(command.tripId()).orElseThrow(TripNotFoundException::new);
        if (trip.status() != TripStatus.OPEN) {
            throw new DomainException(BookingErrorCode.BOOKING_409_001);
        }

        var distinctSeats = command.seatNos().stream().distinct().toList();
        if (distinctSeats.size() != command.seatNos().size()) {
            throw new DomainException(BookingErrorCode.BOOKING_400_005);
        }
        for (int seatNo : distinctSeats) {
            if (seatNo < 1 || seatNo > trip.seatsTotal()) {
                throw new DomainException(BookingErrorCode.BOOKING_400_005);
            }
        }

        int amountXaf = trip.priceXaf() * distinctSeats.size();
        try {
            return seatHoldService.placeHold(
                    command.tripId(), distinctSeats, command.passengerName(), command.passengerMsisdn(), amountXaf);
        } catch (DataIntegrityViolationException ex) {
            if (SeatHoldExecutor.isSeatConflict(ex)) {
                throw new DomainException(BookingErrorCode.BOOKING_409_001);
            }
            throw ex;
        }
    }
}
