package cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyBranchJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.RouteJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.ScheduleJpa;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AvailableSeats;
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
import java.time.LocalDateTime;
import java.util.UUID;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "routes", source = "routes")
    @Mapping(target = "branches", source = "branches")
    @Mapping(target = "createdAt", source = "createdAt.value")
    AgencyJpa toJpa(Agency agency);

    @AfterMapping
    default void linkRoutesToAgency(@MappingTarget AgencyJpa agencyJpa, Agency agency) {
        linkChildRoutes(agencyJpa);
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", source = "status")
    @Mapping(
            target = "phoneNumber",
            expression = "java(new PhoneNumber(entity.getPhoneCountryCode(), entity.getPhoneNumber()))")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "branches", source = "branches")
    @Mapping(target = "routes", source = "routes")
    @Mapping(target = "createdAt", source = "createdAt")
    Agency toDomain(AgencyJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "agencyId", source = "agencyId.value")
    @Mapping(target = "cityId", source = "cityId.value")
    @Mapping(target = "createdAt", source = "createdAt.value")
    AgencyBranchJpa toJpa(AgencyBranch branch);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "cityId", source = "cityId")
    @Mapping(target = "agencyId", source = "agencyId")
    @Mapping(target = "createdAt.value", source = "createdAt")
    AgencyBranch toDomain(AgencyBranchJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "name.value", source = "name")
    @Mapping(target = "address.value", source = "address")
    @Mapping(target = "cityId.value", source = "cityId")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "agencyId.value", source = "agencyId")
    @Mapping(target = "createdAt.value", source = "createdAt")
    AgencyView.BranchView toView(AgencyBranchJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "name.value", source = "name")
    @Mapping(target = "phoneNumber.countryCode", source = "phoneCountryCode")
    @Mapping(target = "phoneNumber.number", source = "phoneNumber")
    @Mapping(target = "licenseNumber.value", source = "licenseNumber")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt.value", source = "createdAt")
    @Mapping(target = "branches", source = "branches")
    AgencyView1 toAgencyView1(AgencyJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "departureId", source = "departure")
    @Mapping(target = "arrivalId", source = "arrival")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "schedules", source = "schedules")
    @Mapping(target = "agencyId", ignore = true)
    @Mapping(target = "totalSeats", source = "totalSeats")
    RouteJpa toJpa(Route route);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "departure", source = "departureId")
    @Mapping(target = "arrival", source = "arrivalId")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "totalSeats", source = "totalSeats", qualifiedByName = "mapTotalSeats")
    @Mapping(target = "schedules", source = "schedules")
    Route toDomain(RouteJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "departureTime", source = "departureTime")
    @Mapping(target = "totalSeats", source = "totalSeats")
    @Mapping(target = "availableSeats", source = "availableSeats")
    ScheduleJpa toJpa(Schedule schedule);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "departureTime", source = "departureTime")
    @Mapping(target = "totalSeats", source = "totalSeats")
    @Mapping(target = "availableSeats", source = "availableSeats")
    Schedule toDomain(ScheduleJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "agencyId", source = "agencyId")
    @Mapping(target = "originCityId", source = "departureId")
    @Mapping(target = "destinationCityId", source = "arrivalId")
    @Mapping(target = "price", source = "price", qualifiedByName = "mapPrice")
    @Mapping(target = "totalSeats", source = "totalSeats", qualifiedByName = "mapTotalSeats")
    @Mapping(target = "availableSchedules", source = "schedules")
    RouteSearchView toRouteSearchView(RouteJpa entity);

    @Named("mapPrice")
    default RoutePrice mapPrice(int price) {
        return price <= 0 ? null : RoutePrice.ofXaf(price);
    }

    @Named("mapTotalSeats")
    default TotalSeats mapTotalSeats(int totalSeats) {
        return totalSeats <= 0 ? null : new TotalSeats(totalSeats);
    }

    @Named("mapAvailableSeats")
    default AvailableSeats mapAvailableSeats(int availableSeats) {
        return availableSeats < 0 ? null : new AvailableSeats(availableSeats);
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "departureTime", source = "departureTime")
    @Mapping(target = "totalSeats", source = "totalSeats", qualifiedByName = "mapTotalSeats")
    @Mapping(target = "availableSeats", source = "availableSeats", qualifiedByName = "mapAvailableSeats")
    RouteSearchView.ScheduleView toScheduleView(ScheduleJpa entity);

    default int map(AvailableSeats value) {
        return value == null ? 0 : value.value();
    }

    default String map(AgencyName value) {
        return value == null ? null : value.value();
    }

    default String map(LicenceNumber value) {
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

    default int map(RoutePrice value) {
        return value == null ? 0 : value.amountXaf();
    }

    default int map(TotalSeats value) {
        return value == null ? 0 : value.value();
    }

    default AgencyId mapToAgencyId(UUID id) {
        return id != null ? new AgencyId(id) : null;
    }

    default AgencyName mapToAgencyName(String name) {
        return name == null ? null : new AgencyName(name);
    }

    default LicenceNumber mapToLicenceNumber(String licenseNumber) {
        return licenseNumber == null ? null : new LicenceNumber(licenseNumber);
    }

    default RouteId mapToRouteId(UUID id) {
        return id != null ? new RouteId(id) : null;
    }

    default ScheduleId mapToScheduleId(UUID id) {
        return id != null ? new ScheduleId(id) : null;
    }

    default BranchId mapToBranchId(UUID id) {
        return id != null ? new BranchId(id) : null;
    }

    default RoutePrice mapToRoutePrice(int price) {
        return RoutePrice.ofXaf(price);
    }

    default TotalSeats mapToTotalSeats(int totalSeats) {
        return new TotalSeats(totalSeats);
    }

    default AvailableSeats mapToAvailableSeats(int availableSeats) {
        return new AvailableSeats(availableSeats);
    }

    default CreatedAt mapToCreatedAt(LocalDateTime createdAt) {
        return createdAt == null ? null : CreatedAt.reconstitute(createdAt);
    }

    default UUID mapCityId(CityId id) {
        return id != null ? id.value() : null;
    }

    default CityId mapToCityId(UUID id) {
        return id != null ? new CityId(id) : null;
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneCountryCode", source = "phoneNumber.countryCode")
    @Mapping(target = "phoneNumber", source = "phoneNumber.number")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "branches", source = "branches")
    @Mapping(target = "routes", source = "routes")
    @Mapping(target = "createdAt", source = "createdAt.value")
    void fromAgencyDomain(@MappingTarget AgencyJpa agencyJpa, Agency agency);

    @AfterMapping
    default void linkRoutesToAgencyOnUpdate(@MappingTarget AgencyJpa agencyJpa, Agency agency) {
        linkChildRoutes(agencyJpa);
    }

    default void linkChildRoutes(AgencyJpa agencyJpa) {
        if (agencyJpa.getId() == null || agencyJpa.getRoutes() == null) {
            return;
        }
        agencyJpa.getRoutes().forEach(route -> route.setAgencyId(agencyJpa.getId()));
    }
}
