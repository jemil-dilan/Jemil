package cm.jemil.booking.application.inbound.usecase;

import cm.jemil.booking.domain.exception.BookingErrorCode;
import cm.jemil.booking.domain.trip.TripRepository;
import cm.jemil.booking.domain.trip.TripSearchView;
import cm.jemil.shared.exception.DomainException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchTripsUseCaseImpl implements SearchTripsUseCase {

    private static final int MINUTES_BEFORE_DEPARTURE = 30;

    private final TripRepository tripRepository;
    private final Clock clock;

    @Override
    public List<TripSearchView> execute(UUID originCityId, UUID destinationCityId, LocalDate serviceDate) {
        if (Objects.equals(originCityId, destinationCityId)) {
            throw new DomainException(BookingErrorCode.BOOKING_400_001);
        }
        var today = LocalDate.now(clock);
        if (serviceDate.isBefore(today)) {
            throw new DomainException(BookingErrorCode.BOOKING_400_002);
        }

        var earliestDeparture = OffsetDateTime.now(clock).plusMinutes(MINUTES_BEFORE_DEPARTURE);
        return tripRepository.search(originCityId, destinationCityId, serviceDate, earliestDeparture);
    }
}
