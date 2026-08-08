package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCaseImpl;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.views.AgencyView;
import cm.jemil.agency.domain.agency.views.AgencyView.BranchView;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchAddress;
import cm.jemil.agency.domain.branch.BranchName;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AddressDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyBranchDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateAgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RouteDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RouteSearchItemDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.ScheduleDTO;
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
public interface AgencyRestMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber.number", source = "phoneNumber.number")
    @Mapping(target = "phoneNumber.countryCode", source = "phoneNumber.countryCode")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "commissionRate", source = "commissionRate")
    RegisterAgencyUseCaseImpl.Command toCreationCommand(CreateAgencyDTO registerAgencyDTO);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber.countryCode", source = "phoneNumber.countryCode")
    @Mapping(target = "phoneNumber.number", source = "phoneNumber.number")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "commissionRate", source = "commissionRate")
    @Mapping(target = "createdAt", source = "createdAt.value")
    @Mapping(target = "branches", source = "branches")
    @Mapping(target = "routeCount", ignore = true)
    @Mapping(target = "addbranchesItem", ignore = true)
    AgencyDTO toDto(AgencyView.AgencyView1 agencyView1);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "isActive", source = "active")
    AgencyBranchDTO toDto(AgencyBranch branch);

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

    default AgencyBranchDTO toBranchDto(BranchView branch) {
        var dto = new AgencyBranchDTO();
        dto.setId(branch.id());
        dto.setName(branch.name());
        if (branch.address() != null) {
            var addrDto = new AddressDTO();
            addrDto.setDistrict(branch.address());
            dto.setAddress(addrDto);
        }
        dto.setIsActive(branch.active());
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
        dto.setOriginCityName(route.getDeparture().value());
        dto.setDestinationCityName(route.getArrival().value());
        dto.setPrice(route.getPrice().price().doubleValue());
        dto.setActive(true);
        return dto;
    }

    default RouteSearchItemDTO toRouteSearchDto(RouteSearchView view) {
        var dto = new RouteSearchItemDTO();
        dto.setId(view.id());
        dto.setAgencyId(view.agencyId());
        dto.setAgencyName(view.agencyName());
        dto.setOriginCityName(view.originCityName());
        dto.setDestinationCityName(view.destinationCityName());
        dto.setPrice(view.price().price().doubleValue());
        dto.setTotalSeats(view.totalSeats().value());
        dto.setActive(view.active());
        dto.setAvailableSchedules(toScheduleDTO(view.availableSchedules()));
        return dto;
    }

    private List<ScheduleDTO> toScheduleDTO(List<RouteSearchView.ScheduleView> schedules) {
        return schedules.stream()
                .map(schedule -> {
                    var dto = new ScheduleDTO();
                    dto.setId(schedule.id());
                    dto.setDepartureTime(schedule.departureTime());
                    dto.setTotalSeats(schedule.totalSeats().value());
                    dto.setAvailableSeats(schedule.availableSeats().value());
                    return dto;
                })
                .toList();
    }

    default double map(cm.jemil.agency.domain.agency.RoutePrice price) {
        return price == null ? 0 : price.price().doubleValue();
    }

    default int map(cm.jemil.agency.domain.agency.TotalSeats totalSeats) {
        return totalSeats == null ? 0 : totalSeats.value();
    }

    default int map(cm.jemil.agency.domain.agency.AvailableSeats availableSeats) {
        return availableSeats == null ? 0 : availableSeats.value();
    }
}
