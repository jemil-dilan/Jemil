package cm.jemil.booking.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingExpiryScheduler {

    @Scheduled(fixedDelayString = "${booking.expiry-check-interval:120000}")
    public void expirePendingBookings() {
        log.debug("Checking for expired pending bookings...");
    }
}
