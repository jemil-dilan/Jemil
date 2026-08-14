package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.CreateCityUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllCitiesUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllCitiesUseCaseImpl.Query;
import cm.jemil.generated.agency.adapter.rest.inbound.api.CityApi;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateCityDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CityController implements CityApi {

    private final CreateCityUseCase createCityUseCase;
    private final GetAllCitiesUseCase getAllCitiesUseCase;
    private final CityRestMapper restMapper;

    @Override
    public ResponseEntity<CreationResponseDTO> createCity(@Valid CreateCityDTO createCityDTO) {
        var response = createCityUseCase.execute(restMapper.toDomain(createCityDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toCreationResponse(response.value()));
    }

    @Override
    public ResponseEntity<PageResponseDTO> getAllCities(@Valid Integer page, @Valid Integer size) {
        var cities = getAllCitiesUseCase.execute(new Query(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(restMapper.toDto(cities));
    }
}
