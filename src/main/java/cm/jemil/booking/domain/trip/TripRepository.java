package cm.jemil.booking.domain.trip;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository {
    List<TripSearchView> search(
            UUID originCityId, UUID destinationCityId, LocalDate serviceDate, OffsetDateTime earliestDeparture);

    Optional<TripDetails> findById(UUID tripId);

    /**
     * Persists a newly materialised trip. Returns {@code false} when a matching trip already
     * exists for the same template and service date (unique index {@code trip_once_per_template_date}).
     */
    boolean tryInsertTrip(TripToCreate trip);

    record TripDetails(UUID id, int priceXaf, int seatsTotal, String status) {}
}
