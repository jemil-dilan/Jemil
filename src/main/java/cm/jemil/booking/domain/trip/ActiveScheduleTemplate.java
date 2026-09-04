package cm.jemil.booking.domain.trip;

import java.time.LocalTime;
import java.util.UUID;

/** Active schedule template joined with route agency and bus capacity. */
public record ActiveScheduleTemplate(
        UUID id,
        UUID routeId,
        UUID busId,
        UUID agencyId,
        LocalTime departureTime,
        short daysOfWeek,
        int priceXaf,
        String travelClass,
        int seatsTotal) {}
