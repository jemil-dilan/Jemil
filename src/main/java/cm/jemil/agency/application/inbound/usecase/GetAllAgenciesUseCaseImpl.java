package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.city.CityName;
import cm.jemil.shared.utils.PageData;
import cm.jemil.shared.utils.PaginationFetchRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class GetAllAgenciesUseCaseImpl implements GetAllAgenciesUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public Response execute(Query query) {
        PageData<AgencyView1> agencyView1PageData =
                agencyRepository.loadAllAgency(query.cityName(), query.pageRequest());
        return new Response(
                agencyView1PageData.elements(),
                agencyView1PageData.total(),
                agencyView1PageData.totalPages(),
                query.size(),
                query.page());
    }

    public record Query(@Nullable String city, int page, int size) {
        private @Nullable CityName cityName() {
            return Optional.ofNullable(city).map(CityName::new).orElse(null);
        }

        private PaginationFetchRequest pageRequest() {
            return new PaginationFetchRequest(size, page);
        }
    }

    public record Response(
            List<AgencyView1> allAgencies, long totalElements, int totalPages, int size, int pageNumber) {}
}
