package cm.jemil.booking.inventory;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BookingJpa;
import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.SeatAssignmentJpa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
class SeatHoldExecutor {

    private static final String HELD = "HELD";
    private static final String CHANNEL_WEB = "WEB";

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    HoldOutcome hold(UUID tripId, int seatNo, String passengerName) {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var bookingId = UUID.randomUUID();

        var booking = new BookingJpa();
        booking.setId(bookingId);
        booking.setRef("JML-" + bookingId.toString().substring(0, 8).toUpperCase());
        booking.setTripId(tripId);
        booking.setChannel(CHANNEL_WEB);
        booking.setPassengerName(passengerName);
        booking.setAmountXaf(0);
        booking.setStatus(HELD);
        booking.setHoldExpiresAt(now.plusMinutes(15));
        booking.setCreatedAt(now);
        entityManager.persist(booking);

        var assignment = new SeatAssignmentJpa();
        assignment.setId(UUID.randomUUID());
        assignment.setTripId(tripId);
        assignment.setSeatNo(seatNo);
        assignment.setBookingId(bookingId);
        assignment.setStatus(HELD);
        assignment.setCreatedAt(now);
        entityManager.persist(assignment);

        try {
            entityManager.flush();
        } catch (RuntimeException ex) {
            if (isSeatConflict(ex)) {
                throw new SeatUnavailableException();
            }
            throw ex;
        }

        return HoldOutcome.HELD;
    }

    private static boolean isSeatConflict(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof DataIntegrityViolationException) {
                return true;
            }
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
