package cm.nyi.booking.application.inbound.usecase;

import cm.nyi.booking.domain.trip.TripSeatMap;
import java.util.UUID;

public interface GetTripSeatMapUseCase {

    TripSeatMap execute(UUID tripId);
}
