package cm.jemil.booking.application.inbound.usecase;

import cm.jemil.booking.domain.trip.TripRepository;
import cm.jemil.booking.domain.trip.TripSeatMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetTripSeatMapUseCaseImpl implements GetTripSeatMapUseCase {

    private final TripRepository tripRepository;

    @Override
    public TripSeatMap execute(UUID tripId) {
        return tripRepository.findSeatMap(tripId);
    }
}
