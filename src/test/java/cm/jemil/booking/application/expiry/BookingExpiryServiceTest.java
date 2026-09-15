package cm.jemil.booking.application.expiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.booking.domain.booking.BookingExpiryRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BookingExpiryServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-09-15T12:00:00Z"), ZoneOffset.UTC);

    @Mock
    private BookingExpiryRepository bookingExpiryRepository;

    private BookingExpiryService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new BookingExpiryService(bookingExpiryRepository, FIXED_CLOCK);
    }

    @Test
    void expireHoldsReleasesExpiredBookingsAndSeats() {
        var bookingId = UUID.randomUUID();
        when(bookingExpiryRepository.findExpiredHeldBookingIds(any())).thenReturn(List.of(bookingId));

        int released = service.expireHolds();

        assertThat(released).isEqualTo(1);
        verify(bookingExpiryRepository).expireHold(bookingId);
    }

    @Test
    void expireHoldsDoesNothingWhenNoneExpired() {
        when(bookingExpiryRepository.findExpiredHeldBookingIds(any())).thenReturn(List.of());

        int released = service.expireHolds();

        assertThat(released).isZero();
        verify(bookingExpiryRepository, never()).expireHold(any());
    }
}
