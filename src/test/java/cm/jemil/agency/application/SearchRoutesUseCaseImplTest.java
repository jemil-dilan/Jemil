package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.SearchRoutesUseCaseImpl;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.Arrival;
import cm.jemil.agency.domain.agency.AvailableSeats;
import cm.jemil.agency.domain.agency.Departure;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.agency.TotalSeats;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SearchRoutesUseCaseImplTest {

    @Mock
    private AgencyRepository agencyRepository;

    private SearchRoutesUseCaseImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SearchRoutesUseCaseImpl(agencyRepository);
    }

    @Test
    void shouldDelegateToRepository() {
        var origin = "Douala";
        var destination = "Yaoundé";
        var view = new RouteSearchView(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Global Voyages",
                origin,
                destination,
                new RoutePrice(BigDecimal.valueOf(5000)),
                new TotalSeats(40),
                true,
                List.of(new RouteSearchView.ScheduleView(UUID.randomUUID(), LocalDateTime.now(), new TotalSeats(40), new AvailableSeats(40))));
        when(agencyRepository.searchRoutes(new Departure(origin), new Arrival(destination)))
                .thenReturn(List.of(view));

        var result = service.execute(new Departure(origin), new Arrival(destination));

        assertThat(result).containsExactly(view);
        verify(agencyRepository).searchRoutes(new Departure(origin), new Arrival(destination));
    }

    @Test
    void shouldReturnEmptyListWhenNoRouteMatches() {
        when(agencyRepository.searchRoutes(new Departure("Douala"), new Arrival("Bafoussam")))
                .thenReturn(List.of());

        var result = service.execute(new Departure("Douala"), new Arrival("Bafoussam"));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowWhenOriginEqualsDestination() {
        assertThatThrownBy(() -> service.execute(new Departure("Douala"), new Arrival("Douala")))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("code", AgencyErrorCode.AGENCY_400_012.getCode());
    }

    @Test
    void shouldThrowWhenOriginEqualsDestinationCaseInsensitive() {
        assertThatThrownBy(() -> service.execute(new Departure("Douala"), new Arrival("DOUALA")))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("code", AgencyErrorCode.AGENCY_400_012.getCode());
    }

    @Test
    void shouldHandleMultipleResults() {
        var origin = "Douala";
        var destination = "Yaoundé";
        var view1 = new RouteSearchView(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Agency 1",
                origin,
                destination,
                new RoutePrice(BigDecimal.valueOf(5000)),
                new TotalSeats(40),
                true,
                List.of());
        var view2 = new RouteSearchView(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Agency 2",
                origin,
                destination,
                new RoutePrice(BigDecimal.valueOf(6000)),
                new TotalSeats(50),
                true,
                List.of());
        
        when(agencyRepository.searchRoutes(new Departure(origin), new Arrival(destination)))
                .thenReturn(List.of(view1, view2));

        var result = service.execute(new Departure(origin), new Arrival(destination));

        assertThat(result).containsExactly(view1, view2);
    }

    @Test
    void shouldPreserveScheduleInformation() {
        var origin = "Douala";
        var destination = "Yaoundé";
        var schedule1 = new RouteSearchView.ScheduleView(
                UUID.randomUUID(),
                LocalDateTime.now().plusHours(1),
                new TotalSeats(40),
                new AvailableSeats(20));
        var schedule2 = new RouteSearchView.ScheduleView(
                UUID.randomUUID(),
                LocalDateTime.now().plusHours(2),
                new TotalSeats(40),
                new AvailableSeats(15));
        
        var view = new RouteSearchView(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Agency",
                origin,
                destination,
                new RoutePrice(BigDecimal.valueOf(5000)),
                new TotalSeats(40),
                true,
                List.of(schedule1, schedule2));
        
        when(agencyRepository.searchRoutes(new Departure(origin), new Arrival(destination)))
                .thenReturn(List.of(view));

        var result = service.execute(new Departure(origin), new Arrival(destination));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).availableSchedules()).containsExactly(schedule1, schedule2);
    }
}
