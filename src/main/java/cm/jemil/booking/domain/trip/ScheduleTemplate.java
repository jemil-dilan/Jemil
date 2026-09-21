package cm.jemil.booking.domain.trip;

import cm.jemil.booking.domain.bus.BusId;
import cm.jemil.shared.utils.CreatedAt;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Domain entity representing a schedule template.
 */
@Getter
@Setter
public class ScheduleTemplate {

    private final ScheduleTemplateId id;
    private final UUID routeId;
    private final BusId busId;
    private final LocalTime departureTime;
    private final short daysOfWeek;
    private final PriceXaf priceXaf;
    private final TravelClass travelClass;
    private boolean active;
    private final CreatedAt createdAt;

    private ScheduleTemplate(
            ScheduleTemplateId id,
            UUID routeId,
            BusId busId,
            LocalTime departureTime,
            short daysOfWeek,
            PriceXaf priceXaf,
            TravelClass travelClass,
            boolean active,
            CreatedAt createdAt) {
        this.id = Objects.requireNonNull(id, "Schedule template ID cannot be null");
        this.routeId = Objects.requireNonNull(routeId, "Route ID cannot be null");
        this.busId = Objects.requireNonNull(busId, "Bus ID cannot be null");
        this.departureTime = Objects.requireNonNull(departureTime, "Departure time cannot be null");
        this.daysOfWeek = daysOfWeek;
        this.priceXaf = Objects.requireNonNull(priceXaf, "Price cannot be null");
        this.travelClass = Objects.requireNonNull(travelClass, "Travel class cannot be null");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
    }

    public static ScheduleTemplate create(
            UUID routeId,
            BusId busId,
            LocalTime departureTime,
            short daysOfWeek,
            PriceXaf priceXaf,
            TravelClass travelClass,
            boolean active,
            CreatedAt createdAt) {
        return new ScheduleTemplate(
                ScheduleTemplateId.generate(),
                routeId,
                busId,
                departureTime,
                daysOfWeek,
                priceXaf,
                travelClass,
                active,
                createdAt);
    }

    public static ScheduleTemplate reconstitute(
            ScheduleTemplateId id,
            UUID routeId,
            BusId busId,
            LocalTime departureTime,
            short daysOfWeek,
            PriceXaf priceXaf,
            TravelClass travelClass,
            boolean active,
            CreatedAt createdAt) {
        return new ScheduleTemplate(
                id, routeId, busId, departureTime, daysOfWeek, priceXaf, travelClass, active, createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleTemplate that = (ScheduleTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
