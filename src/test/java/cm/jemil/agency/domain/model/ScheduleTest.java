package cm.jemil.agency.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.jemil.agency.domain.agency.AvailableSeats;
import cm.jemil.agency.domain.agency.Schedule;
import cm.jemil.agency.domain.agency.ScheduleId;
import cm.jemil.agency.domain.agency.TotalSeats;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ScheduleTest {

    @Test
    void shouldCreateScheduleWithAllSeatsAvailable() {
        var schedule = Schedule.of(ScheduleId.generate(), LocalDateTime.of(2026, 6, 15, 8, 0), new TotalSeats(50));

        assertThat(schedule.getId()).isNotNull();
        assertThat(schedule.getDepartureTime()).isEqualTo(LocalDateTime.of(2026, 6, 15, 8, 0));
        assertThat(schedule.getTotalSeats().value()).isEqualTo(50);
        assertThat(schedule.getAvailableSeats().value()).isEqualTo(50);
    }

    @Test
    void shouldCreateScheduleWithCustomAvailableSeats() {
        var schedule = new Schedule(
                ScheduleId.generate(), 
                LocalDateTime.of(2026, 6, 15, 8, 0), 
                new TotalSeats(50), 
                new AvailableSeats(30));

        assertThat(schedule.getAvailableSeats().value()).isEqualTo(30);
    }

    @Test
    void shouldCheckAvailableSeats() {
        var schedule = new Schedule(
                ScheduleId.generate(), 
                LocalDateTime.of(2026, 6, 15, 8, 0), 
                new TotalSeats(50), 
                new AvailableSeats(30));

        assertThat(schedule.hasAvailableSeats(10)).isTrue();
        assertThat(schedule.hasAvailableSeats(30)).isTrue();
        assertThat(schedule.hasAvailableSeats(31)).isFalse();
    }

    @Test
    void shouldReturnNewScheduleWhenBookingSeats() {
        var schedule = new Schedule(
                ScheduleId.generate(), 
                LocalDateTime.of(2026, 6, 15, 8, 0), 
                new TotalSeats(50), 
                new AvailableSeats(30));

        var newSchedule = schedule.bookSeats(5);

        assertThat(newSchedule.getAvailableSeats().value()).isEqualTo(25);
        // Original schedule should be unchanged (immutability)
        assertThat(schedule.getAvailableSeats().value()).isEqualTo(30);
    }

    @Test
    void shouldThrowWhenBookingMoreSeatsThanAvailable() {
        var schedule = new Schedule(
                ScheduleId.generate(), 
                LocalDateTime.of(2026, 6, 15, 8, 0), 
                new TotalSeats(50), 
                new AvailableSeats(10));

        assertThatThrownBy(() -> schedule.bookSeats(11))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(AgencyErrorCode.AGENCY_400_002.getMessage());
    }

    @Test
    void shouldThrowWhenBookingNegativeSeats() {
        var schedule = new Schedule(
                ScheduleId.generate(), 
                LocalDateTime.of(2026, 6, 15, 8, 0), 
                new TotalSeats(50), 
                new AvailableSeats(10));

        assertThatThrownBy(() -> schedule.bookSeats(-1))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(AgencyErrorCode.AGENCY_400_002.getMessage());
    }

    @Test
    void shouldBookAllRemainingSeats() {
        var schedule = new Schedule(
                ScheduleId.generate(), 
                LocalDateTime.of(2026, 6, 15, 8, 0), 
                new TotalSeats(50), 
                new AvailableSeats(10));

        var newSchedule = schedule.bookSeats(10);

        assertThat(newSchedule.getAvailableSeats().value()).isZero();
    }

    @Test
    void shouldThrowWhenAvailableSeatsExceedTotalSeats() {
        assertThatThrownBy(() -> 
                new Schedule(
                        ScheduleId.generate(), 
                        LocalDateTime.of(2026, 6, 15, 8, 0), 
                        new TotalSeats(50), 
                        new AvailableSeats(51)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(AgencyErrorCode.AGENCY_400_002.getMessage());
    }
}
