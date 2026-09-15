package cm.jemil.booking.application;

import cm.jemil.booking.application.expiry.BookingExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Cron entry-point for hold expiry. In e2e tests the tick is simulated by calling
 * {@link #expirePendingBookings()} directly — the same code path as production.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingExpiryScheduler {

    private final BookingExpiryService bookingExpiryService;

    @Scheduled(fixedDelayString = "${booking.expiry-check-interval:60000}")
    public void expirePendingBookings() {
        int released = bookingExpiryService.expireHolds();
        log.debug("Scheduled booking expiry completed with {} released hold(s)", released);
    }
}
