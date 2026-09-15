package cm.jemil.booking.application.expiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BookingJpa;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.BookingSpringRepository;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.SeatAssignmentSpringRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BookingExpiryServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-09-15T12:00:00Z"), ZoneOffset.UTC);

    @Mock
    private BookingSpringRepository bookingSpringRepository;

    @Mock
    private SeatAssignmentSpringRepository seatAssignmentSpringRepository;

    private BookingExpiryService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new BookingExpiryService(bookingSpringRepository, seatAssignmentSpringRepository, FIXED_CLOCK);
    }

    @Test
    void expireHoldsReleasesExpiredBookingsAndSeats() {
        var booking = new BookingJpa();
        booking.setId(UUID.randomUUID());
        booking.setStatus("HELD");
        booking.setHoldExpiresAt(OffsetDateTime.parse("2026-09-15T11:00:00Z"));
        when(bookingSpringRepository.findExpiredHolds(any())).thenReturn(List.of(booking));

        int released = service.expireHolds();

        assertThat(released).isEqualTo(1);
        ArgumentCaptor<BookingJpa> captor = ArgumentCaptor.forClass(BookingJpa.class);
        verify(bookingSpringRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("EXPIRED");
        verify(seatAssignmentSpringRepository).releaseHeldSeatsForBooking(booking.getId());
    }

    @Test
    void expireHoldsDoesNothingWhenNoneExpired() {
        when(bookingSpringRepository.findExpiredHolds(any())).thenReturn(List.of());

        int released = service.expireHolds();

        assertThat(released).isZero();
        verify(bookingSpringRepository, never()).save(any());
        verify(seatAssignmentSpringRepository, never()).releaseHeldSeatsForBooking(any());
    }
}
