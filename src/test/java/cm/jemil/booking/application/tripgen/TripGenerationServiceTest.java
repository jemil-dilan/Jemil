package cm.jemil.booking.application.tripgen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.booking.domain.trip.ActiveScheduleTemplate;
import cm.jemil.booking.domain.trip.ScheduleTemplateRepository;
import cm.jemil.booking.domain.trip.TripRepository;
import cm.jemil.booking.domain.trip.TripToCreate;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TripGenerationServiceTest {

    /** 2026-09-05 is a Saturday in Africa/Douala (UTC+1). */
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-09-05T12:00:00Z"), ZoneOffset.UTC);

    private static final LocalDate WINDOW_START = LocalDate.of(2026, 9, 5);

    private static final ActiveScheduleTemplate MONDAY_ONLY_TEMPLATE = new ActiveScheduleTemplate(
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
            UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
            UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
            LocalTime.of(8, 30),
            (short) 0b0000001, // Monday only
            4500,
            "ECONOMY",
            50);

    private static final ActiveScheduleTemplate DAILY_TEMPLATE = new ActiveScheduleTemplate(
            UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"),
            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
            UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
            UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
            LocalTime.of(9, 15),
            (short) 0b1111111, // every day
            6500,
            "VIP",
            40);

    @Mock
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @Mock
    private TripRepository tripRepository;

    private TripGenerationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TripGenerationService(scheduleTemplateRepository, tripRepository, FIXED_CLOCK);
    }

    @Test
    void matchesDayOfWeekReturnsTrueForConfiguredDay() {
        assertThat(TripGenerationService.matchesDayOfWeek((short) 0b0000001, DayOfWeek.MONDAY))
                .isTrue();
        assertThat(TripGenerationService.matchesDayOfWeek((short) 0b1000000, DayOfWeek.SUNDAY))
                .isTrue();
    }

    @Test
    void matchesDayOfWeekReturnsFalseForUnconfiguredDay() {
        assertThat(TripGenerationService.matchesDayOfWeek((short) 0b0000001, DayOfWeek.TUESDAY))
                .isFalse();
        assertThat(TripGenerationService.matchesDayOfWeek((short) 0b0000010, DayOfWeek.MONDAY))
                .isFalse();
    }

    @Test
    void matchesDayOfWeekReturnsFalseForEmptyMask() {
        assertThat(TripGenerationService.matchesDayOfWeek((short) 0, DayOfWeek.MONDAY))
                .isFalse();
    }

    @Test
    void generateRollingWindowCreatesNothingWithoutTemplates() {
        when(scheduleTemplateRepository.findActiveTemplates()).thenReturn(List.of());

        int created = service.generateRollingWindow();

        assertThat(created).isZero();
        verify(tripRepository, never()).tryInsertTrip(any());
    }

    @Test
    void generateRollingWindowMaterialisesDailyTemplateForFullWindow() {
        when(scheduleTemplateRepository.findActiveTemplates()).thenReturn(List.of(DAILY_TEMPLATE));
        when(tripRepository.tryInsertTrip(any(TripToCreate.class))).thenReturn(true);

        int created = service.generateRollingWindow();

        assertThat(created).isEqualTo(TripGenerationService.WINDOW_DAYS);
        var captor = ArgumentCaptor.forClass(TripToCreate.class);
        verify(tripRepository, times(TripGenerationService.WINDOW_DAYS)).tryInsertTrip(captor.capture());

        var trips = captor.getAllValues();
        assertThat(trips).hasSize(TripGenerationService.WINDOW_DAYS);
        for (TripToCreate trip : trips) {
            assertThat(trip.id()).isNotNull();
            assertThat(trip.templateId()).isEqualTo(DAILY_TEMPLATE.id());
            assertThat(trip.agencyId()).isEqualTo(DAILY_TEMPLATE.agencyId());
            assertThat(trip.routeId()).isEqualTo(DAILY_TEMPLATE.routeId());
            assertThat(trip.busId()).isEqualTo(DAILY_TEMPLATE.busId());
            assertThat(trip.priceXaf()).isEqualTo(DAILY_TEMPLATE.priceXaf());
            assertThat(trip.travelClass()).isEqualTo(DAILY_TEMPLATE.travelClass());
            assertThat(trip.seatsTotal()).isEqualTo(DAILY_TEMPLATE.seatsTotal());
            assertThat(trip.status()).isEqualTo("OPEN");
            assertThat(trip.seatsSold()).isZero();
            assertThat(trip.serviceDate()).isBetween(WINDOW_START, WINDOW_START.plusDays(13));
            assertThat(trip.departureAt().toLocalDate()).isEqualTo(trip.serviceDate());
            assertThat(trip.departureAt().getOffset().getTotalSeconds()).isEqualTo(3600);
            assertThat(trip.departureAt().toLocalTime()).isEqualTo(DAILY_TEMPLATE.departureTime());
        }
    }

    @Test
    void generateRollingWindowMaterialisesOnlyConfiguredWeekdays() {
        when(scheduleTemplateRepository.findActiveTemplates()).thenReturn(List.of(MONDAY_ONLY_TEMPLATE));
        when(tripRepository.tryInsertTrip(any(TripToCreate.class))).thenReturn(true);

        int created = service.generateRollingWindow();

        assertThat(created).isEqualTo(2);
        var captor = ArgumentCaptor.forClass(TripToCreate.class);
        verify(tripRepository, times(2)).tryInsertTrip(captor.capture());

        assertThat(captor.getAllValues())
                .extracting(TripToCreate::serviceDate)
                .containsExactly(LocalDate.of(2026, 9, 7), LocalDate.of(2026, 9, 14));
    }

    @Test
    void generateRollingWindowCountsOnlySuccessfulInserts() {
        when(scheduleTemplateRepository.findActiveTemplates()).thenReturn(List.of(MONDAY_ONLY_TEMPLATE));
        when(tripRepository.tryInsertTrip(any(TripToCreate.class))).thenReturn(true, false);

        int created = service.generateRollingWindow();

        assertThat(created).isEqualTo(1);
    }

    @Test
    void generateRollingWindowBuildsDepartureWithDoualaOffset() {
        when(scheduleTemplateRepository.findActiveTemplates()).thenReturn(List.of(MONDAY_ONLY_TEMPLATE));
        when(tripRepository.tryInsertTrip(any(TripToCreate.class))).thenReturn(true);

        service.generateRollingWindow();

        var captor = ArgumentCaptor.forClass(TripToCreate.class);
        verify(tripRepository, times(2)).tryInsertTrip(captor.capture());

        for (TripToCreate trip : captor.getAllValues()) {
            assertThat(trip.departureAt())
                    .isEqualTo(trip.serviceDate()
                            .atTime(MONDAY_ONLY_TEMPLATE.departureTime())
                            .atOffset(ZoneOffset.ofHours(1)));
        }
    }
}
