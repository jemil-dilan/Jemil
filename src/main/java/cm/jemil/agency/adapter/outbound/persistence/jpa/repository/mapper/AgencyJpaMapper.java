package cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyBranchJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.RouteJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.ScheduleJpa;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.RouteId;
import cm.jemil.agency.domain.agency.Schedule;
import cm.jemil.agency.domain.agency.ScheduleId;
import cm.jemil.agency.domain.agency.views.AgencyView;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.city.CityId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgencyJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneCountryCode", source = "phoneNumber.countryCode")
    @Mapping(target = "phoneNumber", source = "phoneNumber.number")
    @Mapping(target = "commissionRate", source = "commissionRate")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt.value")
    @Mapping(target = "branches", ignore = true)
    @Mapping(target = "routes", source = "routes")
    AgencyJpa toJpa(Agency agency);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "agency", ignore = true)
    AgencyBranchJpa toJpa(AgencyBranch branch);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "cityId", source = "city.id")
    AgencyBranch toDomain(AgencyBranchJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber.countryCode", source = "phoneCountryCode")
    @Mapping(target = "phoneNumber.number", source = "phoneNumber")
    @Mapping(target = "commissionRate", source = "commissionRate")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt.value", source = "createdAt")
    @Mapping(target = "branches", source = "branches")
    AgencyView1 toAgencyView1(AgencyJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "cityId", source = "city.id")
    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "active", source = "active")
    AgencyView.BranchView toBranchView(AgencyBranchJpa branchJpa);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber.countryCode", source = "phoneCountryCode")
    @Mapping(target = "phoneNumber.number", source = "phoneNumber")
    @Mapping(target = "commissionRate", source = "commissionRate")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt.value", source = "createdAt")
    @Mapping(target = "branches", source = "branches")
    @Mapping(target = "routes", source = "routes")
    Agency toDomain(AgencyJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "departure", source = "departure")
    @Mapping(target = "arrival", source = "arrival")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "totalSeats", source = "totalSeats")
    @Mapping(target = "schedules", source = "schedules")
    @Mapping(target = "agency", ignore = true)
    RouteJpa toJpa(Route route);

    default Route toDomain(RouteJpa entity) {
        if (entity == null) {
            return null;
        }

        List<Schedule> schedules = entity.getSchedules() == null
                ? new ArrayList<>()
                : new ArrayList<>(
                        entity.getSchedules().stream().map(this::toDomain).toList());

        return new Route(
                mapToRouteId(entity.getId()),
                entity.getDeparture(),
                entity.getArrival(),
                entity.getPrice(),
                entity.getTotalSeats(),
                schedules);
    }

    @Mapping(target = "version", ignore = true)
    ScheduleJpa toJpa(Schedule schedule);

    @Mapping(target = "id", source = "id")
    Schedule toDomain(ScheduleJpa entity);

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

    default UUID map(BranchId id) {
        return id != null ? id.value() : null;
    }

    default BranchId mapToBranchId(UUID id) {
        return id != null ? new BranchId(id) : null;
    }

    default UUID mapCityId(CityId id) {
        return id != null ? id.value() : null;
    }

    default CityId mapToCityId(UUID id) {
        return id != null ? new CityId(id) : null;
    }
}
