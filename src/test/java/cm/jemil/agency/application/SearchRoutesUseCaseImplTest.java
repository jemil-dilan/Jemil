package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.SearchRoutesUseCaseImpl;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.AvailableSeats;
import cm.jemil.agency.domain.agency.RouteId;
import cm.jemil.agency.domain.agency.RoutePrice;
import cm.jemil.agency.domain.agency.ScheduleId;
import cm.jemil.agency.domain.agency.TotalSeats;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
import cm.jemil.agency.domain.city.CityId;
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
        var origin = UUID.randomUUID();
        var destination = UUID.randomUUID();
        var view = new RouteSearchView(
                new RouteId(UUID.randomUUID()),
                new AgencyId(UUID.randomUUID()),
                new CityId(origin),
                new CityId(destination),
                new RoutePrice(BigDecimal.valueOf(5000)),
                new TotalSeats(40),
                List.of(new RouteSearchView.ScheduleView(
                        new ScheduleId(UUID.randomUUID()),
                        LocalDateTime.now(),
                        new TotalSeats(40),
                        new AvailableSeats(40))));
        when(agencyRepository.searchRoutes(new CityId(origin), new CityId(destination)))
                .thenReturn(List.of(view));

        var result = service.execute(origin, destination);

        assertThat(result).containsExactly(view);
        verify(agencyRepository).searchRoutes(new CityId(origin), new CityId(destination));
    }

    @Test
    void shouldReturnEmptyListWhenNoRouteMatches() {
        var origin = UUID.randomUUID();
        var destination = UUID.randomUUID();
        when(agencyRepository.searchRoutes(new CityId(origin), new CityId(destination)))
                .thenReturn(List.of());

        var result = service.execute(origin, destination);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowWhenOriginEqualsDestination() {
        var id = UUID.randomUUID();

        assertThatThrownBy(() -> service.execute(id, id))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("code", AgencyErrorCode.AGENCY_400_012.getCode());
    }

    @Test
    void shouldHandleMultipleResults() {
        var origin = UUID.randomUUID();
        var destination = UUID.randomUUID();
        var view1 = new RouteSearchView(
                new RouteId(UUID.randomUUID()),
                new AgencyId(UUID.randomUUID()),
                new CityId(origin),
                new CityId(destination),
                new RoutePrice(BigDecimal.valueOf(5000)),
                new TotalSeats(40),
                List.of());
        var view2 = new RouteSearchView(
                new RouteId(UUID.randomUUID()),
                new AgencyId(UUID.randomUUID()),
                new CityId(origin),
                new CityId(destination),
                new RoutePrice(BigDecimal.valueOf(6000)),
                new TotalSeats(50),
                List.of());

        when(agencyRepository.searchRoutes(new CityId(origin), new CityId(destination)))
                .thenReturn(List.of(view1, view2));

        var result = service.execute(origin, destination);

        assertThat(result).containsExactly(view1, view2);
    }

    @Test
    void shouldPreserveScheduleInformation() {
        var origin = UUID.randomUUID();
        var destination = UUID.randomUUID();
        var schedule1 = new RouteSearchView.ScheduleView(
                new ScheduleId(UUID.randomUUID()),
                LocalDateTime.now().plusHours(1),
                new TotalSeats(40),
                new AvailableSeats(20));
        var schedule2 = new RouteSearchView.ScheduleView(
                new ScheduleId(UUID.randomUUID()),
                LocalDateTime.now().plusHours(2),
                new TotalSeats(40),
                new AvailableSeats(15));

        var view = new RouteSearchView(
                new RouteId(UUID.randomUUID()),
                new AgencyId(UUID.randomUUID()),
                new CityId(origin),
                new CityId(destination),
                new RoutePrice(BigDecimal.valueOf(5000)),
                new TotalSeats(40),
                List.of(schedule1, schedule2));

        when(agencyRepository.searchRoutes(new CityId(origin), new CityId(destination)))
                .thenReturn(List.of(view));

        var result = service.execute(origin, destination);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).availableSchedules()).containsExactly(schedule1, schedule2);
    }
}
