package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.CreateCityUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.GetAllCitiesUseCaseImpl;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateCityDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.PageResponseDTO;
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
    @Mapping(target = "content", source = "allCities")
    @Mapping(target = "number", source = "pageNumber")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "totalElements", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    PageResponseDTO toDto(GetAllCitiesUseCaseImpl.Response response);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "region", source = "region")
    CreateCityUseCaseImpl.Command toDomain(CreateCityDTO createCityDTO);

    @Mapping(target = "newId", source = "id")
    CreationResponseDTO toCreationResponse(UUID id);
}
