package cm.jemil.booking.application.tripgen;

import cm.jemil.booking.domain.trip.ActiveScheduleTemplate;
import cm.jemil.booking.domain.trip.ScheduleTemplateRepository;
import cm.jemil.booking.domain.trip.TripRepository;
import cm.jemil.booking.domain.trip.TripToCreate;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UC-S-06 — materialise a rolling 14-day window of trips from active schedule templates.
 * Timezone: Africa/Douala. Uniqueness is enforced by DB index trip_once_per_template_date.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TripGenerationService {

    public static final ZoneId AFRICA_DOUALA = ZoneId.of("Africa/Douala");
    public static final int WINDOW_DAYS = 14;

    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final TripRepository tripRepository;
    private final Clock clock;

    @Transactional
    public int generateRollingWindow() {
        var zoneClock = clock.withZone(AFRICA_DOUALA);
        LocalDate today = LocalDate.now(zoneClock);
        var templates = scheduleTemplateRepository.findActiveTemplates();
        int created = 0;

        for (ActiveScheduleTemplate template : templates) {
            for (int offset = 0; offset < WINDOW_DAYS; offset++) {
                LocalDate serviceDate = today.plusDays(offset);
                if (!matchesDayOfWeek(template.daysOfWeek(), serviceDate.getDayOfWeek())) {
                    continue;
                }
                if (tryInsertTrip(template, serviceDate)) {
                    created++;
                }
            }
        }

        if (created == 0) {
            log.warn(
                    "UC-S-06 trip generation created 0 trips (templates={}, windowStart={}). "
                            + "If templates exist and days match, generation may be broken.",
                    templates.size(),
                    today);
        } else {
            log.info("UC-S-06 trip generation created {} trips for window starting {}", created, today);
        }
        return created;
    }

    static boolean matchesDayOfWeek(short daysOfWeekMask, DayOfWeek dayOfWeek) {
        int bit = dayOfWeek.getValue() - 1; // Monday=0 … Sunday=6
        return (daysOfWeekMask & (1 << bit)) != 0;
    }

    private boolean tryInsertTrip(ActiveScheduleTemplate template, LocalDate serviceDate) {
        OffsetDateTime departureAt = OffsetDateTime.of(
                serviceDate, template.departureTime(), AFRICA_DOUALA.getRules().getOffset(serviceDate.atStartOfDay()));

        var trip = new TripToCreate(
                UUID.randomUUID(),
                template.id(),
                template.agencyId(),
                template.routeId(),
                template.busId(),
                departureAt,
                serviceDate,
                template.priceXaf(),
                template.travelClass(),
                "OPEN",
                template.seatsTotal(),
                0);
        return tripRepository.tryInsertTrip(trip);
    }
}
