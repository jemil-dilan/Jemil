package cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.ScheduleTemplateJpa;
import cm.jemil.booking.domain.bus.BusId;
import cm.jemil.booking.domain.trip.PriceXaf;
import cm.jemil.booking.domain.trip.ScheduleTemplate;
import cm.jemil.booking.domain.trip.TravelClass;
import cm.jemil.shared.utils.CreatedAt;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Mapper between ScheduleTemplate domain entity and ScheduleTemplateJpa entity.
 */
@Component
public class ScheduleTemplateJpaMapper {

    public ScheduleTemplateJpa toJpa(ScheduleTemplate domain) {
        Objects.requireNonNull(domain, "ScheduleTemplate domain entity cannot be null");

        ScheduleTemplateJpa jpa = new ScheduleTemplateJpa();
        jpa.setId(domain.getId().value());
        jpa.setRouteId(domain.getRouteId());
        jpa.setBusId(domain.getBusId().value());
        jpa.setDepartureTime(domain.getDepartureTime());
        jpa.setDaysOfWeek(domain.getDaysOfWeek());
        jpa.setPriceXaf(domain.getPriceXafValue());
        jpa.setTravelClass(domain.getTravelClass().name());
        jpa.setActive(domain.isActive());
        return jpa;
    }

    public ScheduleTemplate toDomain(ScheduleTemplateJpa jpa) {
        Objects.requireNonNull(jpa, "ScheduleTemplateJpa entity cannot be null");

        return ScheduleTemplate.create(
                jpa.getRouteId(),
                new BusId(jpa.getBusId()),
                jpa.getDepartureTime(),
                jpa.getDaysOfWeek(),
                new PriceXaf(jpa.getPriceXaf()),
                TravelClass.valueOf(jpa.getTravelClass()),
                jpa.isActive(),
                CreatedAt.reconstitute(jpa.getCreatedAt().toLocalDateTime()));
    }

    public void fromDomain(ScheduleTemplateJpa jpa, ScheduleTemplate domain) {
        Objects.requireNonNull(jpa, "ScheduleTemplateJpa entity cannot be null");
        Objects.requireNonNull(domain, "ScheduleTemplate domain entity cannot be null");

        jpa.setRouteId(domain.getRouteId());
        jpa.setBusId(domain.getBusId().value());
        jpa.setDepartureTime(domain.getDepartureTime());
        jpa.setDaysOfWeek(domain.getDaysOfWeek());
        jpa.setPriceXaf(domain.getPriceXafValue());
        jpa.setTravelClass(domain.getTravelClass().name());
        jpa.setActive(domain.isActive());
    }
}
