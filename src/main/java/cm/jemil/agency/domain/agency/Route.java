package cm.jemil.agency.domain.agency;

import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Route {
    private final RouteId id;
    private final CityId departure;
    private final CityId arrival;
    private final RoutePrice price;
    private final List<Schedule> schedules;

    public Route(RouteId id, CityId departure, CityId arrival, RoutePrice price, List<Schedule> schedules) {
        this.id = id;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.schedules = schedules == null ? new ArrayList<>() : schedules;
    }

    public static Route create(CityId departure, CityId arrival, RoutePrice price) {
        return new Route(RouteId.generate(), departure, arrival, price, new ArrayList<>());
    }

    public void addSchedule(LocalDateTime departureTime, TotalSeats totalSeats) {
        if (departureTime == null) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_009, "Departure time is required");
        }
        schedules.add(Schedule.of(ScheduleId.generate(), departureTime, totalSeats));
    }

    public UUID id() {
        return id.value();
    }

    public List<Schedule> getSchedules() {
        return Collections.unmodifiableList(schedules);
    }
}
