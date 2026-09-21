package cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.ScheduleTemplateJpa;
import cm.jemil.booking.domain.bus.BusId;
import cm.jemil.booking.domain.trip.PriceXaf;
import cm.jemil.booking.domain.trip.ScheduleTemplate;
import cm.jemil.booking.domain.trip.ScheduleTemplateId;
import cm.jemil.booking.domain.trip.TravelClass;
import cm.jemil.shared.utils.CreatedAt;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper between ScheduleTemplate domain entity and ScheduleTemplateJpa entity.
 */
@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleTemplateJpaMapper {

    @Mapping(target = "createdAt", ignore = true)
    ScheduleTemplateJpa toJpa(ScheduleTemplate domain);

    default ScheduleTemplate toDomain(ScheduleTemplateJpa jpa) {
        Objects.requireNonNull(jpa, "ScheduleTemplateJpa entity cannot be null");

        return ScheduleTemplate.reconstitute(
                new ScheduleTemplateId(jpa.getId()),
                jpa.getRouteId(),
                new BusId(jpa.getBusId()),
                jpa.getDepartureTime(),
                jpa.getDaysOfWeek(),
                new PriceXaf(jpa.getPriceXaf()),
                TravelClass.valueOf(jpa.getTravelClass()),
                jpa.isActive(),
                CreatedAt.reconstitute(jpa.getCreatedAt().toLocalDateTime()));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void fromDomain(@MappingTarget ScheduleTemplateJpa jpa, ScheduleTemplate domain);

    default UUID map(ScheduleTemplateId value) {
        return value == null ? null : value.value();
    }

    default UUID map(BusId value) {
        return value == null ? null : value.value();
    }

    default int map(PriceXaf value) {
        return value == null ? 0 : value.amount();
    }

    default OffsetDateTime map(CreatedAt value) {
        return value == null ? null : value.value().atOffset(ZoneOffset.UTC);
    }
}
