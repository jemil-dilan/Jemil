package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.city.CityRepository;
import cm.jemil.agency.domain.city.views.CityView.CityView1;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAllCitiesUseCaseImpl implements GetAllCitiesUseCase {
    private final CityRepository cityRepository;

    @Override
    public List<CityView1> execute() {
        return cityRepository.findAllView1();
    }
}
