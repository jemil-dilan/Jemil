package cm.jemil.agency.adpater.inbound.rest;

import cm.jemil.agency.domain.model.Agency;
import cm.jemil.agency.domain.model.Route;
import cm.jemil.agency.adpater.inbound.rest.AgencyController.AgencyResponse;
import cm.jemil.agency.adpater.inbound.rest.AgencyController.RouteResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper Web : convertit les Aggregates domaine en DTOs HTTP.
 *
 * <p>Ce mapper est dans l'infrastructure web. Il connaît les DTOs
 * et les Aggregates, mais le domaine ne le connaît pas.
 *
 * <p>Note : avec MapStruct tu pourrais auto-générer ce mapper.
 * On le fait manuellement ici pour que tu comprennes ce qui se passe.
 * Tu pourras migrer vers @Mapper MapStruct une fois à l'aise.
 */
@Component
public class AgencyWebMapper {

    public AgencyResponse toResponse(Agency agency) {
        return new AgencyResponse(
                agency.getId().toString(),
                agency.getName(),
                agency.getCity(),
                agency.getContactPhone(),
                agency.getStatus().name(),
                agency.getRoutes().size());
    }

    public RouteResponse toResponse(Route route) {
        return new RouteResponse(
                route.getId().toString(),
                route.getAgencyId().toString(),
                route.getOrigin(),
                route.getDestination(),
                route.getTotalSeats(),
                route.isActive());
    }
}
