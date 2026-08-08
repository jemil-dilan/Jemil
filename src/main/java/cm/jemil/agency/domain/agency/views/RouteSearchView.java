package cm.jemil.agency.domain.agency.views;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RouteSearchView(
        UUID id,
        UUID agencyId,
        String agencyName,
        String originCityName,
        String destinationCityName,
        double price,
        int totalSeats,
        boolean active,
        List<ScheduleView> availableSchedules) {

    public record ScheduleView(UUID id, LocalDateTime departureTime, int totalSeats, int availableSeats) {}
}
