package cm.jemil.agency.domain.agency;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Schedule {
    private final ScheduleId id;
    private LocalDateTime departureTime;
    private int totalSeats;
    private int availableSeats;

    public boolean hasAvailableSeats(int requestedSeats) {
        if (requestedSeats <= 0) {
            return false;
        }
        return availableSeats >= requestedSeats;
    }

    public void bookSeats(int count) {
        if (!hasAvailableSeats(count)) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_002);
        }
        this.availableSeats -= count;
    }
}
