package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.booking.domain.trip.TripToCreate;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.simple.JdbcClient;

class JpaTripRepositoryTest {

    @Mock
    private TripSpringRepository tripSpringRepository;

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private JdbcClient.StatementSpec statementSpec;

    private JpaTripRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new JpaTripRepository(tripSpringRepository, jdbcClient);
        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param(any())).thenReturn(statementSpec);
        when(statementSpec.param(anyInt())).thenReturn(statementSpec);
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
    void tryInsertTripReturnsTrueWhenRowInserted() {
        when(statementSpec.update()).thenReturn(1);

        boolean inserted = repository.tryInsertTrip(sampleTrip());

        assertThat(inserted).isTrue();
        verify(jdbcClient).sql(contains("ON CONFLICT"));
    }

    @Test
    void tryInsertTripReturnsFalseWhenConflictSkipped() {
        when(statementSpec.update()).thenReturn(0);

        boolean inserted = repository.tryInsertTrip(sampleTrip());

        assertThat(inserted).isFalse();
    }
}
