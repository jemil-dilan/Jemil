package cm.jemil.booking.application;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.booking.application.expiry.BookingExpiryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BookingExpirySchedulerTest {

    @Mock
    private BookingExpiryService bookingExpiryService;

    private BookingExpiryScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduler = new BookingExpiryScheduler(bookingExpiryService);
    }

    @Test
    void expirePendingBookingsDelegatesToService() {
        when(bookingExpiryService.expireHolds()).thenReturn(2);

        scheduler.expirePendingBookings();

        verify(bookingExpiryService).expireHolds();
    }
}
