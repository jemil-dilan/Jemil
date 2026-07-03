package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.AddBranchUseCase;
import cm.jemil.agency.application.inbound.usecase.AddRouteUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCase;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCase;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.views.AgencyView;
import cm.jemil.generated.agency.adapter.rest.inbound.api.AgencyApi;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AddRouteDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyBranchDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateAgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateBranchDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.PageResponseDTO;
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
    private final AddRouteUseCase addRouteUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AgencyRestMapper restMapper;

    @Override
    public ResponseEntity<CreationResponseDTO> registerAgency( CreateAgencyDTO registerAgencyDTO) {
        var id = registerAgencyUseCase.execute(restMapper.toCreationCommand(registerAgencyDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toCreationResponse(id.value()));
    }

    @Override
    public ResponseEntity<AgencyDTO> getAgencyById(UUID agencyId) {
        AgencyView.AgencyView1 response = getAgencyByIdUseCase.execute(agencyId);
        return ResponseEntity.status(HttpStatus.OK).body(restMapper.toDto(response));
    }

    @Override
    public ResponseEntity<PageResponseDTO> getAllAgencies(String city, Integer page, Integer size) {
        var agencies = getAllAgenciesUseCase.execute(city, page, size).stream()
                .map(restMapper::toDto)
                .toList();
        var response = new PageResponseDTO();
        response.setContent(List.copyOf(agencies));
        response.setTotalElements(agencies.size());
        response.setTotalPages(1);
        response.setSize(agencies.size());
        response.setNumber(0);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<AgencyBranchDTO> addAgencyBranch(UUID agencyId,  CreateBranchDTO createBranchDTO) {
        var address = createBranchDTO.getAddress();
        var branch =
                addBranchUseCase.execute(agencyId, createBranchDTO.getName(), address, createBranchDTO.getCityId());
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toDto(branch));
    }

    @Override
    public ResponseEntity<RouteDTO> addRouteToAgency(UUID agencyId,  AddRouteDTO addRouteDTO) {
        var route = addRouteUseCase.execute(
                new AgencyId(agencyId),
                addRouteDTO.getOriginCityId().toString(),
                addRouteDTO.getDestinationCityId().toString(),
                addRouteDTO.getPrice(),
                addRouteDTO.getTotalSeats());
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toRouteDto(agencyId, route));
    }
}
