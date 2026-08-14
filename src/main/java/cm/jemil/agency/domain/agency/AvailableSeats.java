package cm.jemil.agency.domain.agency;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.AGENCY_400_002;

import cm.jemil.shared.exception.DomainException;

/**
 * Value object representing the number of available seats on a schedule.
 * Ensures the value is non-negative and does not exceed total seats.
 */
public record AvailableSeats(int value) {
    public AvailableSeats {
        if (value < 0) {
            throw new DomainException(AGENCY_400_002);
        }
    }

    public boolean canAccommodate(int requestedSeats) {
        return value >= requestedSeats && requestedSeats > 0;
    }

    public AvailableSeats subtract(int seatsToBook) {
        if (!canAccommodate(seatsToBook)) {
            throw new DomainException(AGENCY_400_002);
        }
        return new AvailableSeats(value - seatsToBook);
    }
}
