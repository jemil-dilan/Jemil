package cm.jemil.booking.application.tripgen;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TripGenerationSchedulerTest {

    @Mock
    private TripGenerationService tripGenerationService;

    private TripGenerationScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduler = new TripGenerationScheduler(tripGenerationService);
    }

    @Test
    void generateRollingWindowDelegatesToTripGenerationService() {
        when(tripGenerationService.generateRollingWindow()).thenReturn(3);

        scheduler.generateRollingWindow();

        verify(tripGenerationService).generateRollingWindow();
    }
}
