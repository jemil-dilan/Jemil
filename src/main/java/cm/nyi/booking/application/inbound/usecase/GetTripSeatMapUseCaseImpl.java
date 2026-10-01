package cm.nyi.booking.application.inbound.usecase;

import cm.nyi.booking.domain.trip.TripRepository;
import cm.nyi.booking.domain.trip.TripSeatMap;
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
