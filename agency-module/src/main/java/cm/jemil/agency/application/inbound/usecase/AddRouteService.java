package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.model.AgencyId;
import cm.jemil.agency.domain.model.Route;
import cm.jemil.agency.domain.port.in.AddRouteUseCase;
import cm.jemil.agency.domain.port.out.AgencyRepository;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddRouteService implements AddRouteUseCase {

    private final AgencyRepository agencyRepository;

    @Override
    public Optional<Route> addRoute(AgencyId agencyId, AddRouteCommand command) {
        return agencyRepository.findById(agencyId).map(agency -> {
            Route route = agency.addRoute(command.origin(), command.destination(), command.totalSeats());
            agencyRepository.save(agency);
            return route;
        });
    }
}
