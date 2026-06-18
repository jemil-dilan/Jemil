package cm.jemil.agency.adapter.outbond.persistence.jpa.repository.mapper;

import cm.jemil.agency.adapter.outbond.persistence.jpa.entity.AgencyJpaEntity;
import cm.jemil.agency.adapter.outbond.persistence.jpa.entity.RouteJpaEntity;
import cm.jemil.agency.adapter.outbond.persistence.jpa.entity.ScheduleJpaEntity;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.RouteId;
import cm.jemil.agency.domain.agency.Schedule;
import cm.jemil.agency.domain.agency.ScheduleId;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgencyPersistenceMapper {

    @Mapping(target = "routes", source = "routes")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "district", source = "address.district")
    @Mapping(target = "countryCode", source = "phoneNumber.countryCode")
    @Mapping(target = "phoneNumber", source = "phoneNumber.number")
    AgencyJpaEntity toJpaEntity(Agency agency);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.district", source = "district")
    @Mapping(target = "phoneNumber.countryCode", source = "countryCode")
    @Mapping(target = "phoneNumber.number", source = "phoneNumber")
    Agency toDomain(AgencyJpaEntity entity);

    RouteJpaEntity toJpaEntity(Route route);

    @Mapping(target = "id", source = "id")
    Route toDomain(RouteJpaEntity entity);

    @Mapping(target = "version", ignore = true)
    ScheduleJpaEntity toJpaEntity(Schedule schedule);

    @Mapping(target = "id", source = "id")
    Schedule toDomain(ScheduleJpaEntity entity);

    default UUID map(AgencyId id) {
        return id != null ? id.value() : null;
    }

    default AgencyId mapToAgencyId(UUID id) {
        return id != null ? new AgencyId(id) : null;
    }

    default UUID map(RouteId id) {
        return id != null ? id.value() : null;
    }

    default RouteId mapToRouteId(UUID id) {
        return id != null ? new RouteId(id) : null;
    }

    default UUID map(ScheduleId id) {
        return id != null ? id.value() : null;
    }

    default ScheduleId mapToScheduleId(UUID id) {
        return id != null ? new ScheduleId(id) : null;
    }
}
