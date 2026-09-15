package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BookingJpa;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class JpaBookingExpiryRepositoryTest {

    @Mock
    private BookingSpringRepository bookingSpringRepository;

    @Mock
    private SeatAssignmentSpringRepository seatAssignmentSpringRepository;

    private JpaBookingExpiryRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new JpaBookingExpiryRepository(bookingSpringRepository, seatAssignmentSpringRepository);
    }

    @Test
    void findExpiredHeldBookingIdsMapsEntityIds() {
        var id = UUID.randomUUID();
        var booking = new BookingJpa();
        booking.setId(id);
        var now = OffsetDateTime.parse("2026-09-15T12:00:00Z");
        when(bookingSpringRepository.findExpiredHolds(now)).thenReturn(List.of(booking));

        assertThat(repository.findExpiredHeldBookingIds(now)).containsExactly(id);
    }

    @Test
    void expireHoldMarksBookingExpiredAndReleasesSeats() {
        var id = UUID.randomUUID();
        var booking = new BookingJpa();
        booking.setId(id);
        booking.setStatus("HELD");
        when(bookingSpringRepository.findById(id)).thenReturn(Optional.of(booking));

        repository.expireHold(id);

        ArgumentCaptor<BookingJpa> captor = ArgumentCaptor.forClass(BookingJpa.class);
        verify(bookingSpringRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("EXPIRED");
        verify(seatAssignmentSpringRepository).releaseHeldSeatsForBooking(id);
    }
}
