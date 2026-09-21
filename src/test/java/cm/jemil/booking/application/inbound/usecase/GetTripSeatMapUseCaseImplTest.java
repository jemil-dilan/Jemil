package cm.jemil.booking.application.inbound.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.booking.domain.exception.BookingErrorCode;
import cm.jemil.booking.domain.exception.TripNotFoundException;
import cm.jemil.booking.domain.trip.TripRepository;
import cm.jemil.booking.domain.trip.TripSeatMap;
import cm.jemil.shared.exception.DomainException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GetTripSeatMapUseCaseImplTest {

    @Mock
    private TripRepository tripRepository;

    private GetTripSeatMapUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetTripSeatMapUseCaseImpl(tripRepository);
    }

    @Test
    void executeReturnsSeatMapWhenTripExists() {
        var tripId = UUID.randomUUID();
        var seatMap = new TripSeatMap(tripId, 40, "2-2", List.of(7, 12));
        when(tripRepository.findSeatMap(tripId)).thenReturn(seatMap);

        assertThat(useCase.execute(tripId)).isEqualTo(seatMap);
        verify(tripRepository).findSeatMap(tripId);
    }

    @Test
    void executeThrowsWhenTripMissing() {
        var tripId = UUID.randomUUID();
        when(tripRepository.findSeatMap(tripId)).thenThrow(new TripNotFoundException());

        assertThatThrownBy(() -> useCase.execute(tripId))
                .isInstanceOf(DomainException.class)
                .extracting(ex -> ((DomainException) ex).getCode())
                .isEqualTo(BookingErrorCode.BOOKING_404_001.getCode());
    }
}
