package cm.jemil.booking.application.inbound.usecase;

import cm.jemil.booking.domain.trip.TripSeatMap;
import java.util.UUID;

public interface GetTripSeatMapUseCase {

    TripSeatMap execute(UUID tripId);
}
