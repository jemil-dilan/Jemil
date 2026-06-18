package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCase;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCase;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.exception.AgencyNotFoundException;
import cm.jemil.generated.agency.adapter.rest.inbound.api.AgencyApi;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AddRouteDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RegisterAgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RouteDTO;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgencyController implements AgencyApi {

    private final RegisterAgencyUseCase registerAgencyUseCase;
    private final GetAgencyByIdUseCase getAgencyByIdUseCase;
    private final GetAllAgenciesUseCase getAllAgenciesUseCase;
    private final AgencyRestMapper restMapper;

    @Override
    public ResponseEntity<CreationResponseDTO> registerAgency(RegisterAgencyDTO dto) {
        var address = restMapper.toAddress(dto);
        var phoneNumber = restMapper.toPhoneNumber(dto);
        var id = registerAgencyUseCase.register(dto.getName(), address, phoneNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toCreationResponse(id.value()));
    }

    @Override
    public ResponseEntity<List<AgencyDTO>> getAllAgencies(String city) {
        var agencies = getAllAgenciesUseCase.execute(city).stream()
                .map(restMapper::toDto)
                .toList();
        return new ResponseEntity<>(agencies, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AgencyDTO> getAgencyById(UUID agencyId) {
        return getAgencyByIdUseCase
                .execute(new AgencyId(agencyId))
                .map(restMapper::toDto)
                .map(d -> new ResponseEntity<>(d, HttpStatus.OK))
                .orElseThrow(() -> AgencyNotFoundException.forId(agencyId.toString()));
    }

    @Override
    public ResponseEntity<RouteDTO> addRoute(UUID agencyId, AddRouteDTO addRouteDTO) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
