package cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyBranchJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.RouteJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.ScheduleJpa;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.Arrival;
import cm.jemil.agency.domain.agency.AvailableSeats;
import cm.jemil.agency.domain.agency.CommissionRate;
import cm.jemil.agency.domain.agency.Departure;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.RouteId;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.agency.Schedule;
import cm.jemil.agency.domain.agency.ScheduleId;
import cm.jemil.agency.domain.agency.TotalSeats;
import cm.jemil.agency.domain.agency.views.AgencyView;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchAddress;
import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchName;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.shared.utils.CreatedAt;
import cm.jemil.shared.utils.PhoneNumber;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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

    default Agency toDomain(AgencyJpa entity) {
        if (entity == null) {
            return null;
        }
        return new Agency(
                mapToAgencyId(entity.getId()),
                new AgencyName(entity.getName()),
                entity.getStatus(),
                new PhoneNumber(entity.getPhoneCountryCode(), entity.getPhoneNumber()),
                new LicenceNumber(entity.getLicenseNumber()),
                new CommissionRate(entity.getCommissionRate()),
                entity.getBranches() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(entity.getBranches().stream()
                                .map(this::toDomain)
                                .toList()),
                entity.getRoutes() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(
                                entity.getRoutes().stream().map(this::toDomain).toList()),
                new CreatedAt(entity.getCreatedAt()));
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "departure", source = "departure")
    @Mapping(target = "arrival", source = "arrival")
    @Mapping(target = "price", source = "price")
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
                new Departure(entity.getDeparture()),
                new Arrival(entity.getArrival()),
                new RoutePrice(BigDecimal.valueOf(entity.getPrice())),
                schedules);
    }

    default ScheduleJpa toJpa(Schedule schedule) {
        if (schedule == null) {
            return null;
        }
        ScheduleJpa entity = new ScheduleJpa();
        entity.setId(map(schedule.getId()));
        entity.setDepartureTime(schedule.getDepartureTime());
        entity.setTotalSeats(schedule.getTotalSeats().value());
        entity.setAvailableSeats(schedule.getAvailableSeats().value());
        return entity;
    }

    default Schedule toDomain(ScheduleJpa entity) {
        if (entity == null) {
            return null;
        }
        return new Schedule(
                mapToScheduleId(entity.getId()),
                entity.getDepartureTime(),
                new TotalSeats(entity.getTotalSeats()),
                new AvailableSeats(entity.getAvailableSeats()));
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "agencyId", source = "agency.id")
    @Mapping(target = "agencyName", source = "agency.name")
    @Mapping(target = "originCityName", source = "departure")
    @Mapping(target = "destinationCityName", source = "arrival")
    @Mapping(target = "price", source = "price", qualifiedByName = "mapPrice")
    @Mapping(target = "totalSeats", source = "totalSeats", qualifiedByName = "mapTotalSeats")
    @Mapping(target = "active", source = "agency.status")
    @Mapping(target = "availableSchedules", source = "schedules")
    RouteSearchView toRouteSearchView(RouteJpa entity);

    @Named("mapPrice")
    default RoutePrice mapPrice(double price) {
        return price <= 0 ? null : new RoutePrice(java.math.BigDecimal.valueOf(price));
    }

    @Named("mapTotalSeats")
    default TotalSeats mapTotalSeats(int totalSeats) {
        return totalSeats <= 0 ? null : new TotalSeats(totalSeats);
    }

    @Named("mapAvailableSeats")
    default AvailableSeats mapAvailableSeats(int availableSeats) {
        return availableSeats < 0 ? null : new AvailableSeats(availableSeats);
    }

    default RouteSearchView.ScheduleView toScheduleView(ScheduleJpa entity) {
        if (entity == null) {
            return null;
        }
        return new RouteSearchView.ScheduleView(
                entity.getId(),
                entity.getDepartureTime(),
                mapTotalSeats(entity.getTotalSeats()),
                mapAvailableSeats(entity.getAvailableSeats()));
    }

    default double map(CommissionRate commissionRate) {
        return commissionRate == null ? 0 : commissionRate.value();
    }

    default double map(AvailableSeats availableSeats) {
        return availableSeats == null ? 0 : availableSeats.value();
    }

    default boolean map(AgencyStatus status) {
        return status == AgencyStatus.ACTIVE;
    }

    default String map(AgencyName value) {
        return value == null ? null : value.value();
    }

    default String map(LicenceNumber value) {
        return value == null ? null : value.value();
    }

    default String map(Departure value) {
        return value == null ? null : value.value();
    }

    default String map(Arrival value) {
        return value == null ? null : value.value();
    }

    default String map(BranchName value) {
        return value == null ? null : value.value();
    }

    default String map(BranchAddress value) {
        return value == null ? null : value.value();
    }

    default BranchName mapToBranchName(String value) {
        return new BranchName(value);
    }

    default BranchAddress mapToBranchAddress(String value) {
        return new BranchAddress(value);
    }

    default double map(RoutePrice value) {
        return value == null ? 0 : value.price().doubleValue();
    }

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
