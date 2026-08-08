package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.Arrival;
import cm.jemil.agency.domain.agency.Departure;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.RouteId;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.agency.TotalSeats;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class RouteTest {

    @Test
    void shouldCreateRoute() {
        var route = new Route(
                RouteId.generate(),
                new Departure("Douala"),
                new Arrival("Yaoundé"),
                new RoutePrice(BigDecimal.valueOf(5000)),
                new ArrayList<>());

        assertThat(route.getId()).isNotNull();
        assertThat(route.getDeparture().value()).isEqualTo("Douala");
        assertThat(route.getArrival().value()).isEqualTo("Yaoundé");
        assertThat(route.getPrice().price()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        assertThat(route.getSchedules()).isEmpty();
    }

    @Test
    void shouldAddScheduleToRoute() {
        var route = new Route(
                RouteId.generate(),
                new Departure("Douala"),
                new Arrival("Yaoundé"),
                new RoutePrice(BigDecimal.valueOf(5000)),
                new ArrayList<>());

        route.addSchedule(LocalDateTime.of(2026, 6, 15, 8, 0), new TotalSeats(50));

        assertThat(route.getSchedules()).hasSize(1);
        var schedule = route.getSchedules().getFirst();
        assertThat(schedule.getDepartureTime()).isEqualTo(LocalDateTime.of(2026, 6, 15, 8, 0));
        assertThat(schedule.getTotalSeats().value()).isEqualTo(50);
        assertThat(schedule.getAvailableSeats().value()).isEqualTo(50);
    }

    @Test
    void shouldAddMultipleSchedules() {
        var route = new Route(
                RouteId.generate(),
                new Departure("Douala"),
                new Arrival("Yaoundé"),
                new RoutePrice(BigDecimal.valueOf(5000)),
                new ArrayList<>());

        route.addSchedule(LocalDateTime.of(2026, 6, 15, 8, 0), new TotalSeats(50));
        route.addSchedule(LocalDateTime.of(2026, 6, 15, 14, 0), new TotalSeats(40));

        assertThat(route.getSchedules()).hasSize(2);
    }
}
