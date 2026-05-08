package cm.jemil.agency.adpater.inbound.rest;

import cm.jemil.agency.domain.model.Agency;
import cm.jemil.agency.domain.model.AgencyId;
import cm.jemil.agency.domain.port.in.AddRouteUseCase;
import cm.jemil.agency.domain.port.in.AddRouteUseCase.AddRouteCommand;
import cm.jemil.agency.domain.port.in.GetAgencyUseCase;
import cm.jemil.agency.domain.port.in.RegisterAgencyUseCase;
import cm.jemil.agency.domain.port.in.RegisterAgencyUseCase.RegisterAgencyCommand;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import cm.milou.generated.jemil.agency.adapter.rest.inbound.api.AgencyApi;
import cm.milou.generated.jemil.agency.adapter.rest.inbound.dto.AddRouteDTO;
import cm.milou.generated.jemil.agency.adapter.rest.inbound.dto.AgencyDTO;
import cm.milou.generated.jemil.agency.adapter.rest.inbound.dto.RegisterAgencyDTO;
import cm.milou.generated.jemil.agency.adapter.rest.inbound.dto.RouteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Adapter entrant : Controller REST pour le module Agency.
 *
 * <p>Ce controller :
 * 1. Reçoit les requêtes HTTP
 * 2. Traduit les DTOs en Commands via le mapper
 * 3. Appelle les ports entrants (use cases) — jamais les services directement
 * 4. Traduit les Aggregates domaine en réponses HTTP
 *
 * <p>Il ne contient AUCUNE logique métier.
 * Si tu trouves un if() ici qui concerne le métier, c'est un bug d'architecture.
 *
 * <p>Note : les DTOs request/response sont générés par OpenAPI Generator
 * depuis agency-api.yml. Cette classe implémentera l'interface générée
 * une fois que tu auras défini le spec YAML.
 */
@RestController
@RequiredArgsConstructor
public class AgencyController implements AgencyApi {

    private final AddRouteUseCase addRouteUseCase;
    private final RegisterAgencyUseCase registerAgencyUseCase;
    private final GetAgencyUseCase getAgencyUseCase;
    private final AgencyWebMapper mapper;


    @Override
    public ResponseEntity<RouteDTO> addRoute(UUID agencyId, AddRouteDTO addRouteDTO) {
        return addRouteUseCase
            .addRoute(AgencyId.from(agencyId), command)
            .map(route -> {
                return ResponseEntity.status(CREATED).body(mapper.toResponse(route));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<AgencyDTO> getAgencyById(UUID agencyId) {

        return getAgencyUseCase
            .findById(AgencyId.from(agencyId))
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<AgencyDTO>> getAllAgencies(String city) {
        List<Agency> agencies = city != null ? getAgencyUseCase.findByCity(city) : getAgencyUseCase.findAllActive();

        return ResponseEntity.ok(agencies.stream().map(mapper::toResponse).toList());
    }

    @Override
    public ResponseEntity<AgencyDTO> registerAgency(RegisterAgencyDTO registerAgencyDTO) {

        RegisterAgencyCommand command =
            new RegisterAgencyCommand(request.name(), request.city(), request.contactPhone());

        Agency agency = registerAgencyUseCase.registerAgency(command);

        return ResponseEntity.status(CREATED).body(mapper.toResponse(agency));
    }
}
