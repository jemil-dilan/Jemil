package cm.jemil.agency.domain.agency.views;

import cm.jemil.agency.domain.agency.AvailableSeats;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.agency.TotalSeats;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RouteSearchView(
        UUID id,
        UUID agencyId,
        String agencyName,
        String originCityName,
        String destinationCityName,
        RoutePrice price,
        TotalSeats totalSeats,
        boolean active,
        List<ScheduleView> availableSchedules) {

    public record ScheduleView(UUID id, LocalDateTime departureTime, TotalSeats totalSeats, AvailableSeats availableSeats) {}
}
