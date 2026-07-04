package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.CreateCityUseCaseImpl;
import cm.jemil.agency.domain.city.views.CityView.CityView1;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CityDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateCityDTO;
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

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "region", source = "region.value")
    CityDTO toDto(CityView1 view);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "region", source = "region")
    CreateCityUseCaseImpl.Command toDomain(CreateCityDTO createCityDTO);

    @Mapping(target = "newId", source = "id")
    CreationResponseDTO toCreationResponse(UUID id);
}
