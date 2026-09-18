package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.ScheduleTemplateJpa;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleTemplateSpringRepository extends JpaRepository<ScheduleTemplateJpa, UUID> {

    List<ScheduleTemplateJpa> findByActive(boolean active);

    List<ScheduleTemplateJpa> findByRouteId(UUID routeId);

    List<ScheduleTemplateJpa> findByBusId(UUID busId);

    @Query(value = """
                    SELECT CAST(st.id AS varchar) AS id,
                           CAST(st.route_id AS varchar) AS routeId,
                           CAST(st.bus_id AS varchar) AS busId,
                           CAST(r.c_agency_id AS varchar) AS agencyId,
                           st.departure_time AS departureTime,
                           st.days_of_week AS daysOfWeek,
                           st.price_xaf AS priceXaf,
                           st.travel_class AS travelClass,
                           b.seat_count AS seatsTotal
                    FROM schedule_templates st
                    JOIN t_routes r ON r.c_id = st.route_id
                    JOIN buses b ON b.id = st.bus_id
                    WHERE st.active = true
                    """, nativeQuery = true)
    List<ActiveTemplateProjection> findActiveTemplateRows();

    default List<cm.jemil.booking.domain.trip.ActiveScheduleTemplate> findActiveTemplates() {
        return findActiveTemplateRows().stream()
                .map(row -> new cm.jemil.booking.domain.trip.ActiveScheduleTemplate(
                        UUID.fromString(row.getId()),
                        UUID.fromString(row.getRouteId()),
                        UUID.fromString(row.getBusId()),
                        UUID.fromString(row.getAgencyId()),
                        row.getDepartureTime(),
                        row.getDaysOfWeek(),
                        row.getPriceXaf(),
                        row.getTravelClass(),
                        row.getSeatsTotal()))
                .toList();
    }

    interface ActiveTemplateProjection {
        String getId();

        String getRouteId();

        String getBusId();

        String getAgencyId();

        LocalTime getDepartureTime();

        Short getDaysOfWeek();

        Integer getPriceXaf();

        String getTravelClass();

        Integer getSeatsTotal();
    }
}
