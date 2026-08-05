package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.AddBranchUseCase;
import cm.jemil.agency.application.inbound.usecase.AddRouteUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllBranchesByAgencyUseCase;
import cm.jemil.agency.application.inbound.usecase.GetBranchByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCase;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.views.AgencyView;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
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
    private final GetBranchByIdUseCase getBranchByIdUseCase;
    private final GetAllBranchesByAgencyUseCase getAllBranchesByAgencyUseCase;
    private final AgencyRestMapper restMapper;

    @Override
    public ResponseEntity<CreationResponseDTO> registerAgency(CreateAgencyDTO registerAgencyDTO) {
        var response = registerAgencyUseCase.execute(restMapper.toCreationCommand(registerAgencyDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toCreationResponse(response.value()));
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
    public ResponseEntity<AgencyBranchDTO> addAgencyBranch(UUID agencyId, CreateBranchDTO createBranchDTO) {
        var response = addBranchUseCase.execute(
                agencyId, createBranchDTO.getName(), createBranchDTO.getAddress(), createBranchDTO.getCityId());
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toDto(response));
    }

    @Override
    public ResponseEntity<RouteDTO> addRouteToAgency(UUID agencyId, AddRouteDTO addRouteDTO) {
        var route = addRouteUseCase.execute(
                new AgencyId(agencyId),
                addRouteDTO.getOriginCityId().toString(),
                addRouteDTO.getDestinationCityId().toString(),
                addRouteDTO.getPrice(),
                addRouteDTO.getTotalSeats());
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toRouteDto(agencyId, route));
    }

    @Override
    public ResponseEntity<AgencyBranchDTO> updateBranch(UUID branchId, AgencyBranchDTO agencyBranchDTO) {
        // TODO: Implement updateBranch use case
        // For now, return not implemented
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<AgencyBranchDTO> getBranchById(UUID branchId) {
        var branch = getBranchByIdUseCase
                .execute(new cm.jemil.agency.domain.branch.BranchId(branchId))
                .orElseThrow(() -> new cm.jemil.shared.exception.DomainException(AgencyErrorCode.BRANCH_404_001));
        return ResponseEntity.ok(restMapper.toDto(branch));
    }

    @Override
    public ResponseEntity<Void> deleteBranch(UUID branchId) {
        // TODO: Implement deleteBranch use case
        // For now, return not implemented
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<List<AgencyBranchDTO>> getAgencyBranches(UUID agencyId) {
        var branches = getAllBranchesByAgencyUseCase.execute(new AgencyId(agencyId));
        var dtos = branches.stream().map(restMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }
}
