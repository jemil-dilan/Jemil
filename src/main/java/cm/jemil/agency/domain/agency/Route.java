package cm.jemil.agency.domain.agency;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class Route {
    private final RouteId id;
    private String departure;
    private String arrival;
    private double price;
    private int totalSeats;
    private final List<Schedule> schedules;

    public Route(RouteId id, String departure, String arrival, double price, List<Schedule> schedules) {
        this(id, departure, arrival, price, 0, schedules);
    }

    public Route(RouteId id, String departure, String arrival, double price, int totalSeats, List<Schedule> schedules) {
        validate(departure, arrival, price);
        if (totalSeats < 0) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_008);
        }
        this.id = id;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.totalSeats = totalSeats;
        this.schedules = schedules == null ? new ArrayList<>() : schedules;
    }

    public static Route create(String departure, String arrival, double price) {
        return new Route(RouteId.generate(), departure, arrival, price, new ArrayList<>());
    }

    public static Route create(String departure, String arrival, double price, int totalSeats) {
        if (totalSeats <= 0) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_008);
        }
        return new Route(RouteId.generate(), departure, arrival, price, totalSeats, new ArrayList<>());
    }

    public void addSchedule(LocalDateTime departureTime, int totalSeats) {
        if (departureTime == null || totalSeats <= 0) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_008);
        }
        schedules.add(new Schedule(ScheduleId.generate(), departureTime, totalSeats, totalSeats));
    }

    public List<Schedule> getSchedules() {
        return Collections.unmodifiableList(schedules);
    }

    private static void validate(String departure, String arrival, double price) {
        if (departure == null || departure.isBlank() || arrival == null || arrival.isBlank()) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_006);
        }
        if (price <= 0) {
            throw new DomainException(AgencyErrorCode.AGENCY_400_007);
        }
    }
}
