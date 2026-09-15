package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.domain.trip.TripRepository;
import cm.jemil.booking.domain.trip.TripSearchView;
import cm.jemil.booking.domain.trip.TripToCreate;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaTripRepository implements TripRepository {

    private final TripSpringRepository tripSpringRepository;
    private final JdbcClient jdbcClient;

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
        // ON CONFLICT avoids aborting the surrounding Postgres transaction on duplicate
        // (catching DataIntegrityViolationException is not enough under PG).
        int inserted = jdbcClient
                .sql(
                        """
                        INSERT INTO trips (
                            id, template_id, agency_id, route_id, bus_id,
                            departure_at, service_date, price_xaf, travel_class,
                            status, seats_total, seats_sold
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT (template_id, service_date)
                            WHERE template_id IS NOT NULL
                        DO NOTHING
                        """)
                .param(trip.id())
                .param(trip.templateId())
                .param(trip.agencyId())
                .param(trip.routeId())
                .param(trip.busId())
                .param(trip.departureAt())
                .param(trip.serviceDate())
                .param(trip.priceXaf())
                .param(trip.travelClass())
                .param(trip.status())
                .param(trip.seatsTotal())
                .param(trip.seatsSold())
                .update();
        return inserted > 0;
    }
}
