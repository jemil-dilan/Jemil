package cm.jemil.agency.domain.agency;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Immutable entity representing a schedule for a route.
 * Uses value objects for domain primitives to ensure validation and type safety.
 */
@Getter
public final class Schedule {
    private final ScheduleId id;
    private final LocalDateTime departureTime;
    private final TotalSeats totalSeats;
    private final AvailableSeats availableSeats;

    public Schedule(
            ScheduleId id,
            LocalDateTime departureTime,
            TotalSeats totalSeats,
            AvailableSeats availableSeats) {
        if (departureTime == null) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_009, "Departure time is required");
        }
        if (availableSeats.value() > totalSeats.value()) {
            throw new DomainException(
                    AgencyErrorCode.AGENCY_400_002);
        }
        this.id = id;
        this.departureTime = departureTime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    /**
     * Factory method to create a schedule with all seats available.
     */
    public static Schedule of(
            ScheduleId id, 
            LocalDateTime departureTime, 
            TotalSeats totalSeats) {
        return new Schedule(id, departureTime, totalSeats, new AvailableSeats(totalSeats.value()));
    }

    /**
     * Returns a new Schedule with the specified number of seats booked.
     * This maintains immutability - the original Schedule is not modified.
     */
    public Schedule bookSeats(int count) {
        if (count <= 0) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_002);
        }
        if (!hasAvailableSeats(count)) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_002);
        }
        return new Schedule(
                id, departureTime, totalSeats, availableSeats.subtract(count));
    }

    public boolean hasAvailableSeats(int requestedSeats) {
        return availableSeats.canAccommodate(requestedSeats);
    }
}

