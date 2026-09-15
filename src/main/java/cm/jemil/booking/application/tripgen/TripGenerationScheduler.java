package cm.jemil.booking.application.tripgen;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripGenerationScheduler {

    private final TripGenerationService tripGenerationService;

    @Scheduled(cron = "${booking.trip-generation.cron:0 15 1 * * *}", zone = "Africa/Douala")
    public void generateRollingWindow() {
        int created = tripGenerationService.generateRollingWindow();
        log.debug("Scheduled trip generation completed with {} newly materialised trips", created);
    }
}
