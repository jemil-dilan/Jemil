package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.AddBranchUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCaseImpl.Response;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCaseImpl;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.agency.views.AgencyView.BranchView;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.agency.domain.branch.BranchAddress;
import cm.jemil.agency.domain.branch.BranchName;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AddressDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyBranchDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateAgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateBranchDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.PageResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RouteDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.ScheduleDTO;
import java.util.List;
import java.util.Objects;
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
public interface AgencyRestMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber.number", source = "phoneNumber.number")
    @Mapping(target = "phoneNumber.countryCode", source = "phoneNumber.countryCode")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    RegisterAgencyUseCaseImpl.Command toCreationCommand(CreateAgencyDTO registerAgencyDTO);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "phoneNumber.countryCode", source = "phoneNumber.countryCode")
    @Mapping(target = "phoneNumber.number", source = "phoneNumber.number")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "licenseNumber", source = "licenseNumber.value")
    @Mapping(target = "createdAt", source = "createdAt.value")
    @Mapping(target = "branches", source = "branches")
    @Mapping(target = "routeCount", ignore = true)
    @Mapping(target = "addbranchesItem", ignore = true)
    AgencyDTO toDto(AgencyView1 agencyView1);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "createdAt", source = "createdAt.value")
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "cityId", source = "cityId.value")
    @Mapping(target = "agencyId", source = "agencyId.value")
    AgencyBranchDTO toDto(BranchView branch);

    @Mapping(target = "content", source = "allAgencies")
    @Mapping(target = "number", source = "pageNumber")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "totalElements", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    PageResponseDTO toDto(Response response);

    default String map(BranchName value) {
        return value == null ? null : value.value();
    }

    default String map(BranchAddress value) {
        return value == null ? null : value.value();
    }

    default AddressDTO map(String address) {
        if (address == null) {
            return null;
        }
        var dto = new AddressDTO();
        dto.setDistrict(address);
        return dto;
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "newId", source = "id")
    CreationResponseDTO toCreationResponse(UUID id);

    default RouteDTO toRouteDto(UUID agencyId, UUID originCityId, UUID destinationCityId, Route route) {
        var dto = new RouteDTO();
        dto.setId(route.getId().value());
        dto.setAgencyId(agencyId);
        dto.setOriginCityId(originCityId);
        dto.setDestinationCityId(destinationCityId);
        dto.setOriginCityId(route.getDeparture().value());
        dto.setOriginCityId(route.getArrival().value());
        dto.setPrice(route.getPrice().price().doubleValue());
        return dto;
    }

    default RouteDTO toRouteSearchDto(RouteSearchView view) {
        var dto = new RouteDTO();
        dto.setId(view.id().value());
        dto.setAgencyId(view.agencyId().value());
        dto.setOriginCityId(view.originCityId().value());
        dto.setDestinationCityId(view.destinationCityId().value());
        dto.setPrice(view.price().price().doubleValue());
        dto.setTotalSeats(view.totalSeats().value());
        dto.setAvailableSchedules(toScheduleDTO(view.availableSchedules()));
        return dto;
    }

    private List<ScheduleDTO> toScheduleDTO(List<RouteSearchView.ScheduleView> schedules) {
        return schedules.stream()
                .map(schedule -> {
                    var dto = new ScheduleDTO();
                    dto.setId(schedule.id().value());
                    dto.setDepartureTime(schedule.departureTime());
                    dto.setTotalSeats(schedule.totalSeats().value());
                    dto.setAvailableSeats(schedule.availableSeats().value());
                    return dto;
                })
                .toList();
    }

    default double map(RoutePrice price) {
        return Objects.isNull(price)  ? 0 : price.price().doubleValue();
    }

    default int map(cm.jemil.agency.domain.agency.TotalSeats totalSeats) {
        return totalSeats == null ? 0 : totalSeats.value();
    }

    default int map(cm.jemil.agency.domain.agency.AvailableSeats availableSeats) {
        return availableSeats == null ? 0 : availableSeats.value();
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "createBranchDTO.name")
    @Mapping(target = "cityId", source = "createBranchDTO.cityId")
    @Mapping(target = "address", source = "createBranchDTO.address")
    @Mapping(target = "agencyId", source = "agencyId")
    AddBranchUseCaseImpl.Command toCommand(UUID agencyId, CreateBranchDTO createBranchDTO);
}
