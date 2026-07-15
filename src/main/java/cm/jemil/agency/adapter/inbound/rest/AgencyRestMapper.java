package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCaseImpl;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.views.AgencyView;
import cm.jemil.agency.domain.agency.views.AgencyView.BranchView;
import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AddressDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyBranchDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateAgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RouteDTO;
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

    default RouteDTO toRouteDto(UUID agencyId, Route route) {
        var dto = new RouteDTO();
        dto.setId(route.getId().value());
        dto.setAgencyId(agencyId);
        dto.setOriginCityName(route.getDeparture());
        dto.setDestinationCityName(route.getArrival());
        dto.setTotalSeats(route.getTotalSeats());
        dto.setPrice(route.getPrice());
        dto.setActive(true);
        return dto;
    }
}
