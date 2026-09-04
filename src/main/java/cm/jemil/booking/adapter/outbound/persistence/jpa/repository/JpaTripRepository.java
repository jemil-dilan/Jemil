package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.TripJpa;
import cm.jemil.booking.domain.trip.TripRepository;
import cm.jemil.booking.domain.trip.TripSearchView;
import cm.jemil.booking.domain.trip.TripToCreate;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaTripRepository implements TripRepository {

    private final TripSpringRepository tripSpringRepository;

    @Override
    public List<TripSearchView> search(
            UUID originCityId, UUID destinationCityId, LocalDate serviceDate, OffsetDateTime earliestDeparture) {
        return tripSpringRepository.searchTrips(originCityId, destinationCityId, serviceDate, earliestDeparture);
    }

    @Override
    public Optional<TripDetails> findById(UUID tripId) {
        return tripSpringRepository
                .findById(tripId)
                .map(trip -> new TripDetails(trip.getId(), trip.getPriceXaf(), trip.getSeatsTotal(), trip.getStatus()));
    }

    @Override
    public boolean tryInsertTrip(TripToCreate trip) {
        try {
            var entity = new TripJpa();
            entity.setId(trip.id());
            entity.setTemplateId(trip.templateId());
            entity.setAgencyId(trip.agencyId());
            entity.setRouteId(trip.routeId());
            entity.setBusId(trip.busId());
            entity.setDepartureAt(trip.departureAt());
            entity.setServiceDate(trip.serviceDate());
            entity.setPriceXaf(trip.priceXaf());
            entity.setTravelClass(trip.travelClass());
            entity.setStatus(trip.status());
            entity.setSeatsTotal(trip.seatsTotal());
            entity.setSeatsSold(trip.seatsSold());
            tripSpringRepository.saveAndFlush(entity);
            return true;
        } catch (DataIntegrityViolationException ex) {
            return false;
        }
    }
}
