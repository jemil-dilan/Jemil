package cm.jemil.booking.application.inventory;

import cm.jemil.booking.domain.booking.Booking;
import cm.jemil.booking.domain.booking.BookingId;
import cm.jemil.booking.domain.booking.BookingRepository;
import cm.jemil.booking.domain.booking.Channel;
import cm.jemil.booking.domain.booking.PassengerInfo;
import cm.jemil.booking.domain.booking.SeatAssignment;
import cm.jemil.booking.domain.booking.SeatAssignmentRepository;
import cm.jemil.booking.domain.seat.SeatNumber;
import cm.jemil.booking.domain.seat.SeatStatus;
import cm.jemil.booking.domain.trip.TripId;
import cm.jemil.shared.utils.CreatedAt;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatHoldExecutor {

    private static final int HOLD_MINUTES = 10;

    private final BookingRepository bookingRepository;
    private final SeatAssignmentRepository seatAssignmentRepository;

    public SeatHoldExecutor(BookingRepository bookingRepository, SeatAssignmentRepository seatAssignmentRepository) {
        this.bookingRepository = bookingRepository;
        this.seatAssignmentRepository = seatAssignmentRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    BookingHoldResult placeHold(
            UUID tripId, List<Integer> seatNos, String passengerName, String passengerMsisdn, int amountXaf) {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var holdExpiresAt = now.plusMinutes(HOLD_MINUTES);
        var bookingId = BookingId.generate();
        var tripIdVo = new TripId(tripId);

        // Use CM country code (+237) for Cameroon
        // If passengerMsisdn is null or blank, use a placeholder
        var msisdn = passengerMsisdn != null && !passengerMsisdn.isBlank() ? passengerMsisdn : "0000000000";
        var phoneNumber = new cm.jemil.shared.utils.PhoneNumber("237", msisdn);
        var passengerInfo = new PassengerInfo(passengerName, phoneNumber);

        // Create booking with specific ID
        var booking = Booking.hold(
                bookingId, tripIdVo, Channel.ONLINE, passengerInfo, amountXaf, holdExpiresAt, CreatedAt.now());

        // Save booking first
        bookingRepository.save(booking);

        // Save seat assignments
        List<SeatAssignment> seatAssignments = seatNos.stream()
                .map(seatNo -> {
                    var seatNumber = new SeatNumber(seatNo);
                    return SeatAssignment.create(tripIdVo, seatNumber, bookingId, SeatStatus.HELD, CreatedAt.now());
                })
                .toList();

        seatAssignmentRepository.saveAll(seatAssignments);

        return new BookingHoldResult(
                bookingId.value(), booking.getRef().value(), tripId, holdExpiresAt, amountXaf, List.copyOf(seatNos));
    }

    public static boolean isSeatConflict(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof ConstraintViolationException constraintViolation) {
                var sqlState = constraintViolation.getSQLState();
                if ("23505".equals(sqlState)) {
                    return true;
                }
                var constraintName = constraintViolation.getConstraintName();
                if (constraintName != null && constraintName.contains("seat_once_per_trip")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    static final class SeatUnavailableException extends RuntimeException {}
}
