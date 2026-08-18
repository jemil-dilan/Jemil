package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.application.inbound.usecase.AddBranchUseCase;
import cm.jemil.agency.application.inbound.usecase.AddRouteUseCase;
import cm.jemil.agency.application.inbound.usecase.AddRouteUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCaseImpl.Query;
import cm.jemil.agency.application.inbound.usecase.GetAllBranchesByAgencyUseCase;
import cm.jemil.agency.application.inbound.usecase.GetBranchByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCase;
import cm.jemil.agency.application.inbound.usecase.SearchRoutesUseCase;
import cm.jemil.agency.application.inbound.usecase.SuspendAgencyUseCase;
import cm.jemil.generated.agency.adapter.rest.inbound.api.AgencyApi;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AddRouteDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyBranchDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateAgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateBranchDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.PageResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RouteSearchResponseDTO;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgencyController implements AgencyApi {

    private final RegisterAgencyUseCase registerAgencyUseCase;
    private final GetAgencyByIdUseCase getAgencyByIdUseCase;
    private final GetAllAgenciesUseCase getAllAgenciesUseCase;
    private final SearchRoutesUseCase searchRoutesUseCase;
    private final SuspendAgencyUseCase suspendAgencyUseCase;
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
        var response = getAgencyByIdUseCase.execute(agencyId);
        return ResponseEntity.status(HttpStatus.OK).body(restMapper.toDto(response));
    }

    @Override
    public ResponseEntity<Void> suspendAgency(UUID agencyId) {
        suspendAgencyUseCase.execute(agencyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<PageResponseDTO> getAllAgencies(@Nullable String city, Integer page, Integer size) {
        var agencies = getAllAgenciesUseCase.execute(new Query(city, page, size));
        return ResponseEntity.status(HttpStatus.OK).body(restMapper.toDto(agencies));
    }

    @Override
    public ResponseEntity<CreationResponseDTO> addAgencyBranch(UUID agencyId, CreateBranchDTO createBranchDTO) {
        var branch = addBranchUseCase.execute(restMapper.toCommand(agencyId, createBranchDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toCreationResponse(branch));
    }

    @Override
    public ResponseEntity<RouteSearchResponseDTO> searchRoutes(UUID originCityId, UUID destinationCityId) {
        var routes = searchRoutesUseCase.execute(originCityId, destinationCityId).stream()
                .map(restMapper::toRouteSearchDto)
                .toList();
        var response = new RouteSearchResponseDTO();
        response.setContent(routes);
        response.setTotalElements(routes.size());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<Void> addRouteToAgency(UUID agencyId, AddRouteDTO addRouteDTO) {
        addRouteUseCase.execute(new AddRouteUseCaseImpl.Command(
                agencyId,
                addRouteDTO.getOriginCityId(),
                addRouteDTO.getDestinationCityId(),
                addRouteDTO.getPrice().intValue(),
                addRouteDTO.getTotalSeats()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<AgencyBranchDTO> updateBranch(UUID branchId, AgencyBranchDTO agencyBranchDTO) {
        // TODO: Implement updateBranch use case
        // For now, return not implemented
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<AgencyBranchDTO> getBranchById(UUID branchId) {
        var branch = getBranchByIdUseCase.execute(branchId);
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
        var branches = getAllBranchesByAgencyUseCase.execute(agencyId).stream()
                .map(restMapper::toDto)
                .toList();
        return ResponseEntity.ok(branches);
    }
}
