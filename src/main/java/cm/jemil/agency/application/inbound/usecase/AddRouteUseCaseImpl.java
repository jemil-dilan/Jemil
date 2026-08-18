package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.agency.TotalSeats;
import cm.jemil.agency.domain.city.CityId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddRouteUseCaseImpl implements AddRouteUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public void execute(Command command) {
        var agency = agencyRepository.loadById(command.getAgencyId());
        agency.addRoute(command.departure(), command.arrival(), command.getPrice(), command.getTotalSeats());
        agencyRepository.update(agency);
    }

    public record Command(UUID agencyId, UUID originCityId, UUID destinationCityId, int priceXaf, int totalSeats) {
        private AgencyId getAgencyId() {
            return new AgencyId(agencyId);
        }

        public CityId departure() {
            return new CityId(originCityId);
        }

        public CityId arrival() {
            return new CityId(destinationCityId);
        }

        private RoutePrice getPrice() {
            return RoutePrice.ofXaf(priceXaf);
        }

        private TotalSeats getTotalSeats() {
            return new TotalSeats(totalSeats);
        }
    }
}
