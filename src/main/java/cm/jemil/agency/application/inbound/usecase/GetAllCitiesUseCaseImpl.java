package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.city.CityRepository;
import cm.jemil.agency.domain.city.views.CityView.CityView1;
import cm.jemil.shared.utils.PageData;
import cm.jemil.shared.utils.PaginationFetchRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAllCitiesUseCaseImpl implements GetAllCitiesUseCase {
    private final CityRepository cityRepository;

    @Override
    public Response execute(Query query) {
        PageData<CityView1> allView1 = cityRepository.findAllView1(query.getPaginationFetchRequest());
        return new Response(
                allView1.elements(),
                allView1.total(),
                allView1.totalPages(),
                allView1.pageSize(),
                allView1.pageNumber());
    }

    public record Query(int page, int size) {
        private PaginationFetchRequest getPaginationFetchRequest() {
            return new PaginationFetchRequest(page, size);
        }
    }

    public record Response(List<CityView1> allCities, long totalElements, int totalPages, int size, int pageNumber) {}
}
