package cm.jemil.e2e.step;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.booking.application.BookingExpiryScheduler;
import cm.jemil.booking.application.tripgen.TripGenerationScheduler;
import io.cucumber.java8.En;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Cucumber steps that <strong>simulate</strong> scheduler ticks by invoking the same
 * scheduler methods the cron would call in production — without waiting for real time.
 */
public class SchedulerStep implements En {

    private static final UUID SEED_TEMPLATE_ID = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    private static final UUID SEED_TRIP_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    private final AtomicInteger rememberedTemplateTripCount = new AtomicInteger();
    private UUID lastBookingId;

    public SchedulerStep(
            TripGenerationScheduler tripGenerationScheduler,
            BookingExpiryScheduler bookingExpiryScheduler,
            JdbcClient jdbcClient) {

        Given(
                "I remember how many trips exist for the seeded schedule template",
                () -> rememberedTemplateTripCount.set(countTemplateTrips(jdbcClient)));

        When("the trip generation scheduler tick is simulated", tripGenerationScheduler::generateRollingWindow);

        When("the trip generation scheduler tick is simulated again", tripGenerationScheduler::generateRollingWindow);

        Then(
                "trips generated from the seeded schedule template cover at least {int} service dates",
                (Integer minDays) -> {
                    int count = countTemplateTrips(jdbcClient);
                    assertThat(count)
                            .as("generated trips for template %s", SEED_TEMPLATE_ID)
                            .isGreaterThanOrEqualTo(minDays);
                });

        Then("the trip count for the seeded schedule template did not increase", () -> {
            int count = countTemplateTrips(jdbcClient);
            assertThat(count)
                    .as("second tick must not duplicate template/date trips")
                    .isEqualTo(rememberedTemplateTripCount.get());
        });

        And(
                "I remember the trip count for the seeded schedule template",
                () -> rememberedTemplateTripCount.set(countTemplateTrips(jdbcClient)));

        And("I backdate the hold expiry of booking ref {string} to the past", (String refPrefix) -> {
            // Prefer exact last created hold on seeded trip if ref is "last"
            if ("last".equalsIgnoreCase(refPrefix)) {
                lastBookingId = jdbcClient
                        .sql("""
                                SELECT id FROM bookings
                                WHERE trip_id = ? AND status = 'HELD'
                                ORDER BY created_at DESC
                                LIMIT 1
                                """)
                        .param(SEED_TRIP_ID)
                        .query((rs, rowNum) -> UUID.fromString(rs.getString("id")))
                        .single();
            } else {
                lastBookingId = jdbcClient
                        .sql("SELECT id FROM bookings WHERE ref = ?")
                        .param(refPrefix)
                        .query((rs, rowNum) -> UUID.fromString(rs.getString("id")))
                        .single();
            }

            int updated = jdbcClient.sql("""
                            UPDATE bookings
                            SET hold_expires_at = (CURRENT_TIMESTAMP AT TIME ZONE 'UTC') - INTERVAL '1 minute'
                            WHERE id = ?
                            """).param(lastBookingId).update();
            assertThat(updated).isEqualTo(1);
        });

        When("the booking expiry scheduler tick is simulated", bookingExpiryScheduler::expirePendingBookings);

        Then("the backdated booking status is {string}", (String status) -> {
            assertThat(lastBookingId).isNotNull();
            String actual = jdbcClient
                    .sql("SELECT status FROM bookings WHERE id = ?")
                    .param(lastBookingId)
                    .query((rs, rowNum) -> rs.getString("status"))
                    .single();
            assertThat(actual).isEqualTo(status);
        });

        And("seat {int} on the seeded trip is free for a new hold", (Integer seatNo) -> {
            Long heldOrSold = jdbcClient
                    .sql("""
                            SELECT COUNT(*) FROM seat_assignments
                            WHERE trip_id = ? AND seat_no = ? AND status IN ('HELD', 'SOLD')
                            """)
                    .param(SEED_TRIP_ID)
                    .param(seatNo)
                    .query((rs, rowNum) -> rs.getLong(1))
                    .single();
            assertThat(heldOrSold).isZero();
        });
    }

    private static int countTemplateTrips(JdbcClient jdbcClient) {
        return jdbcClient
                .sql("SELECT COUNT(*) FROM trips WHERE template_id = ?")
                .param(SEED_TEMPLATE_ID)
                .query((rs, rowNum) -> rs.getInt(1))
                .single();
    }
}
