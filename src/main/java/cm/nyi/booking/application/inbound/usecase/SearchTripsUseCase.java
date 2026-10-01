package cm.nyi.booking.application.inbound.usecase;

import cm.nyi.booking.domain.trip.TripSearchView;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SearchTripsUseCase {
    List<TripSearchView> execute(UUID originCityId, UUID destinationCityId, LocalDate serviceDate);
}
