package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RegisterAgencyDTO;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AgencyRestMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "routeCount", expression = "java(agency.getRoutes() != null ? agency.getRoutes().size() : 0)")
    @Mapping(target = "address.city", source = "address.city")
    @Mapping(target = "address.district", source = "address.district")
    @Mapping(target = "phoneNumber.countryCode", source = "phoneNumber.countryCode")
    @Mapping(target = "phoneNumber.number", source = "phoneNumber.number")
    AgencyDTO toDto(Agency agency);

    default CreationResponseDTO toCreationResponse(UUID id) {
        var response = new CreationResponseDTO();
        response.setNewId(id);
        return response;
    }

    default Address toAddress(RegisterAgencyDTO dto) {
        if (dto == null || dto.getAddress() == null) {
            return null;
        }
        return new Address(dto.getAddress().getCity(), dto.getAddress().getDistrict());
    }

    default PhoneNumber toPhoneNumber(RegisterAgencyDTO dto) {
        if (dto == null || dto.getPhoneNumber() == null) {
            return null;
        }
        return new PhoneNumber(
                dto.getPhoneNumber().getCountryCode(), dto.getPhoneNumber().getNumber());
    }
}
