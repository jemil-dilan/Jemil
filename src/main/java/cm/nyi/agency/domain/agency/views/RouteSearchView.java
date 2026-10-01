package cm.nyi.agency.domain.agency.views;

import cm.nyi.agency.domain.agency.AgencyId;
import cm.nyi.agency.domain.agency.AvailableSeats;
import cm.nyi.agency.domain.agency.RouteId;
import cm.nyi.agency.domain.agency.RoutePrice;
import cm.nyi.agency.domain.agency.ScheduleId;
import cm.nyi.agency.domain.agency.TotalSeats;
import cm.nyi.agency.domain.city.CityId;
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
