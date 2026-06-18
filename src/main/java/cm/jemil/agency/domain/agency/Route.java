package cm.jemil.agency.domain.agency;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Route {
    private final RouteId id;
    private String departure;
    private String arrival;
    private double price;
    private final List<Schedule> schedules;

    public void addSchedule(LocalDateTime departureTime, int totalSeats) {
        schedules.add(new Schedule(ScheduleId.generate(), departureTime, totalSeats, totalSeats));
    }

    public List<Schedule> getSchedules() {
        return Collections.unmodifiableList(schedules);
    }
}
