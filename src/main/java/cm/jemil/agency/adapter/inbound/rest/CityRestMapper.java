package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.domain.city.views.CityView.CityView1;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CityDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import java.util.UUID;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CityRestMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "region", ignore = true)
    CityDTO toDto(CityView1 view);

    default CreationResponseDTO toCreationResponse(UUID id) {
        var response = new CreationResponseDTO();
        response.setNewId(id);
        return response;
    }
}
