package cm.jemil.agency.domain.agency;

import static org.junit.jupiter.api.Assertions.*;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

class AvailableSeatsTest {

    @Test
    void shouldCreateAvailableSeatsWithValidValue() {
        AvailableSeats seats = new AvailableSeats(50);
        assertEquals(50, seats.value());
    }

    @Test
    void shouldCreateAvailableSeatsWithZero() {
        AvailableSeats seats = new AvailableSeats(0);
        assertEquals(0, seats.value());
    }

    @Test
    void shouldRejectNegativeAvailableSeats() {
        DomainException exception = assertThrows(DomainException.class, () -> new AvailableSeats(-1));
        assertEquals(AgencyErrorCode.AGENCY_400_002.getCode(), exception.getCode());
    }

    @Test
    void shouldCheckCanAccommodate() {
        AvailableSeats seats = new AvailableSeats(10);
        assertTrue(seats.canAccommodate(5));
        assertTrue(seats.canAccommodate(10));
        assertFalse(seats.canAccommodate(11));
        assertFalse(seats.canAccommodate(0));
        assertFalse(seats.canAccommodate(-1));
    }

    @Test
    void shouldSubtractSeats() {
        AvailableSeats seats = new AvailableSeats(10);
        AvailableSeats newSeats = seats.subtract(3);
        assertEquals(7, newSeats.value());
    }

    @Test
    void shouldThrowWhenSubtractingMoreThanAvailable() {
        AvailableSeats seats = new AvailableSeats(10);
        DomainException exception = assertThrows(DomainException.class, () -> seats.subtract(11));
        assertEquals(AgencyErrorCode.AGENCY_400_002.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowWhenSubtractingNegative() {
        AvailableSeats seats = new AvailableSeats(10);
        DomainException exception = assertThrows(DomainException.class, () -> seats.subtract(-1));
        assertEquals(AgencyErrorCode.AGENCY_400_002.getCode(), exception.getCode());
    }
}
