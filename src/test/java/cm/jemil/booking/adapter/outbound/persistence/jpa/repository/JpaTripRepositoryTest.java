package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.TripJpa;
import cm.jemil.booking.domain.trip.TripToCreate;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

class JpaTripRepositoryTest {

    @Mock
    private TripSpringRepository tripSpringRepository;

    private JpaTripRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new JpaTripRepository(tripSpringRepository);
    }

    private TripToCreate sampleTrip() {
        return new TripToCreate(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.of(LocalDate.of(2026, 9, 7), java.time.LocalTime.of(8, 30), ZoneOffset.ofHours(1)),
                LocalDate.of(2026, 9, 7),
                4500,
                "ECONOMY",
                "OPEN",
                50,
                0);
    }

    @Test
    void tryInsertTripMapsDomainFieldsAndSaves() {
        var trip = sampleTrip();
        when(tripSpringRepository.saveAndFlush(any(TripJpa.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean inserted = repository.tryInsertTrip(trip);

        assertThat(inserted).isTrue();
        var captor = ArgumentCaptor.forClass(TripJpa.class);
        verify(tripSpringRepository).saveAndFlush(captor.capture());

        TripJpa saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(trip.id());
        assertThat(saved.getTemplateId()).isEqualTo(trip.templateId());
        assertThat(saved.getAgencyId()).isEqualTo(trip.agencyId());
        assertThat(saved.getRouteId()).isEqualTo(trip.routeId());
        assertThat(saved.getBusId()).isEqualTo(trip.busId());
        assertThat(saved.getDepartureAt()).isEqualTo(trip.departureAt());
        assertThat(saved.getServiceDate()).isEqualTo(trip.serviceDate());
        assertThat(saved.getPriceXaf()).isEqualTo(trip.priceXaf());
        assertThat(saved.getTravelClass()).isEqualTo(trip.travelClass());
        assertThat(saved.getStatus()).isEqualTo(trip.status());
        assertThat(saved.getSeatsTotal()).isEqualTo(trip.seatsTotal());
        assertThat(saved.getSeatsSold()).isEqualTo(trip.seatsSold());
    }

    @Test
    void tryInsertTripReturnsFalseOnUniqueConstraintViolation() {
        var trip = sampleTrip();
        when(tripSpringRepository.saveAndFlush(any(TripJpa.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        boolean inserted = repository.tryInsertTrip(trip);

        assertThat(inserted).isFalse();
        verify(tripSpringRepository).saveAndFlush(any(TripJpa.class));
    }
}
