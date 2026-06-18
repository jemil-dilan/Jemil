package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.jemil.agency.domain.agency.Schedule;
import cm.jemil.agency.domain.agency.ScheduleId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ScheduleTest {

    @Test
    void shouldCreateSchedule() {
        var schedule = new Schedule(ScheduleId.generate(), LocalDateTime.of(2026, 6, 15, 8, 0), 50, 50);

        assertThat(schedule.getId()).isNotNull();
        assertThat(schedule.getDepartureTime()).isEqualTo(LocalDateTime.of(2026, 6, 15, 8, 0));
        assertThat(schedule.getTotalSeats()).isEqualTo(50);
        assertThat(schedule.getAvailableSeats()).isEqualTo(50);
    }

    @Test
    void shouldCheckAvailableSeats() {
        var schedule = new Schedule(ScheduleId.generate(), LocalDateTime.of(2026, 6, 15, 8, 0), 50, 30);

        assertThat(schedule.hasAvailableSeats(10)).isTrue();
        assertThat(schedule.hasAvailableSeats(30)).isTrue();
        assertThat(schedule.hasAvailableSeats(31)).isFalse();
    }

    @Test
    void shouldBookSeatsWhenAvailable() {
        var schedule = new Schedule(ScheduleId.generate(), LocalDateTime.of(2026, 6, 15, 8, 0), 50, 30);

        schedule.bookSeats(5);

        assertThat(schedule.getAvailableSeats()).isEqualTo(25);
    }

    @Test
    void shouldThrowWhenBookingMoreSeatsThanAvailable() {
        var schedule = new Schedule(ScheduleId.generate(), LocalDateTime.of(2026, 6, 15, 8, 0), 50, 10);

        assertThatThrownBy(() -> schedule.bookSeats(11))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not enough seats available");
    }

    @Test
    void shouldBookAllRemainingSeats() {
        var schedule = new Schedule(ScheduleId.generate(), LocalDateTime.of(2026, 6, 15, 8, 0), 50, 10);

        schedule.bookSeats(10);

        assertThat(schedule.getAvailableSeats()).isZero();
    }
}
