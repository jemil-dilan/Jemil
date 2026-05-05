package cm.jemil.agency.infrastructure.web.controller;

import cm.jemil.agency.domain.model.Agency;
import cm.jemil.agency.domain.model.AgencyId;
import cm.jemil.agency.domain.port.in.GetAgencyUseCase;
import cm.jemil.agency.domain.port.in.RegisterAgencyUseCase;
import cm.jemil.agency.domain.port.in.RegisterAgencyUseCase.RegisterAgencyCommand;
import cm.jemil.agency.infrastructure.web.mapper.AgencyWebMapper;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
@RequestMapping("/api/v1/agencies")
public class AgencyController {

    // Injection via INTERFACES (ports) — jamais les implémentations concrètes
    private final RegisterAgencyUseCase registerAgencyUseCase;
    private final GetAgencyUseCase getAgencyUseCase;
    private final AgencyWebMapper mapper;

    public AgencyController(
            RegisterAgencyUseCase registerAgencyUseCase, GetAgencyUseCase getAgencyUseCase, AgencyWebMapper mapper) {
        this.registerAgencyUseCase = registerAgencyUseCase;
        this.getAgencyUseCase = getAgencyUseCase;
        this.mapper = mapper;
    }

    /**
     * POST /api/v1/agencies
     * Enregistre une nouvelle agence partenaire JEMIL.
     */
    @PostMapping
    public ResponseEntity<AgencyResponse> registerAgency(@RequestBody RegisterAgencyRequest request) {

        RegisterAgencyCommand command =
                new RegisterAgencyCommand(request.name(), request.city(), request.contactPhone());

        Agency agency = registerAgencyUseCase.registerAgency(command);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(agency.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(agency));
    }

    /**
     * GET /api/v1/agencies/{id}
     * Récupère une agence par son identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AgencyResponse> getAgency(@PathVariable String id) {
        return getAgencyUseCase
                .findById(AgencyId.from(id))
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/agencies?city=Douala
     * Récupère les agences actives, optionnellement filtrées par ville.
     */
    @GetMapping
    public ResponseEntity<List<AgencyResponse>> getAgencies(@RequestParam(required = false) String city) {

        List<Agency> agencies = city != null ? getAgencyUseCase.findByCity(city) : getAgencyUseCase.findAllActive();

        return ResponseEntity.ok(agencies.stream().map(mapper::toResponse).toList());
    }

    // ── DTOs Request / Response ───────────────────────────────
    // Ces records seront remplacés par les classes générées par OpenAPI
    // une fois que tu auras défini agency-api.yml

    public record RegisterAgencyRequest(String name, String city, String contactPhone) {}

    public record AgencyResponse(
            String id, String name, String city, String contactPhone, String status, int routeCount) {}
}
