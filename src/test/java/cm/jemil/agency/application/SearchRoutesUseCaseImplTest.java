package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.SearchRoutesUseCaseImpl;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.views.RouteSearchView;
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
                5000,
                40,
                true,
                List.of(new RouteSearchView.ScheduleView(UUID.randomUUID(), LocalDateTime.now(), 40, 40)));
        when(agencyRepository.searchRoutes(origin, destination)).thenReturn(List.of(view));

        var result = service.execute(origin, destination);

        assertThat(result).containsExactly(view);
        verify(agencyRepository).searchRoutes(origin, destination);
    }

    @Test
    void shouldReturnEmptyListWhenNoRouteMatches() {
        when(agencyRepository.searchRoutes("Douala", "Bafoussam")).thenReturn(List.of());

        var result = service.execute("Douala", "Bafoussam");

        assertThat(result).isEmpty();
    }
}
