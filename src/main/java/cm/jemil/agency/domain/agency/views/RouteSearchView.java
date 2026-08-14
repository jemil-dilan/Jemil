package cm.jemil.agency.domain.agency.views;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AvailableSeats;
import cm.jemil.agency.domain.agency.RouteId;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.agency.ScheduleId;
import cm.jemil.agency.domain.agency.TotalSeats;
import cm.jemil.agency.domain.city.CityId;
import java.time.LocalDateTime;
import java.util.List;

public record RouteSearchView(
        RouteId id,
        AgencyId agencyId,
        CityId originCityId,
        CityId destinationCityId,
        RoutePrice price,
        TotalSeats totalSeats,
        List<ScheduleView> availableSchedules) {

    public record ScheduleView(
            ScheduleId id, LocalDateTime departureTime, TotalSeats totalSeats, AvailableSeats availableSeats) {}
}
