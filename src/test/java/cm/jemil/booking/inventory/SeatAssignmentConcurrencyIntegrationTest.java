package cm.jemil.booking.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.JemilApplication;
import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BusJpa;
import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.TripJpa;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.BusSpringRepository;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.SeatAssignmentSpringRepository;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.TripSpringRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(classes = JemilApplication.class)
@ActiveProfiles("test")
class SeatAssignmentConcurrencyIntegrationTest {

    private static final UUID SEED_AGENCY_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");
    private static final UUID SEED_ROUTE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("jemil_test")
            .withUsername("jemil")
            .withPassword("jemil_secret");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("jwt.secret", () -> "test-secret-key-that-is-at-least-32-bytes-long-for-hmac");
    }

    @Autowired
    private SeatHoldService seatHoldService;

    @Autowired
    private BusSpringRepository busSpringRepository;

    @Autowired
    private TripSpringRepository tripSpringRepository;

    @Autowired
    private SeatAssignmentSpringRepository seatAssignmentSpringRepository;

    private UUID tripId;

    @BeforeEach
    void seedTrip() {
        var busId = UUID.randomUUID();
        var bus = new BusJpa();
        bus.setId(busId);
        bus.setAgencyId(SEED_AGENCY_ID);
        bus.setLabel("Seed Coach");
        bus.setPlate("CM-SEED-01");
        bus.setSeatCount(40);
        bus.setSeatLayout("2-2");
        busSpringRepository.saveAndFlush(bus);

        tripId = UUID.randomUUID();
        var trip = new TripJpa();
        trip.setId(tripId);
        trip.setAgencyId(SEED_AGENCY_ID);
        trip.setRouteId(SEED_ROUTE_ID);
        trip.setBusId(busId);
        trip.setDepartureAt(OffsetDateTime.of(2026, 8, 20, 8, 0, 0, 0, ZoneOffset.UTC));
        trip.setServiceDate(LocalDate.of(2026, 8, 20));
        trip.setPriceXaf(5000);
        trip.setTravelClass("STANDARD");
        trip.setStatus("OPEN");
        trip.setSeatsTotal(40);
        trip.setSeatsSold(0);
        tripSpringRepository.saveAndFlush(trip);
    }

    @Test
    void onlyOneParallelHoldSucceedsForSameSeat() throws Exception {
        int threads = 20;
        var pool = Executors.newFixedThreadPool(threads);
        var startGate = new CountDownLatch(1);
        var successes = new AtomicInteger();
        var conflicts = new AtomicInteger();

        List<Callable<Void>> tasks = java.util.stream.IntStream.range(0, threads)
                .mapToObj(i -> (Callable<Void>) () -> {
                    startGate.await();
                    var outcome = seatHoldService.tryHoldSeat(tripId, 12, "Passenger " + i);
                    if (outcome == HoldOutcome.HELD) {
                        successes.incrementAndGet();
                    } else {
                        conflicts.incrementAndGet();
                    }
                    return null;
                })
                .toList();

        var futures = tasks.stream().map(pool::submit).toList();
        startGate.countDown();

        for (var future : futures) {
            future.get();
        }
        pool.shutdown();

        assertThat(successes.get()).isEqualTo(1);
        assertThat(conflicts.get()).isEqualTo(19);
        assertThat(seatAssignmentSpringRepository.countByTripIdAndSeatNoAndStatusIn(
                        tripId, 12, List.of("HELD", "SOLD")))
                .isEqualTo(1);
    }
}
