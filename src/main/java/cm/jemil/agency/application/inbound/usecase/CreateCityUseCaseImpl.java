package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.city.City;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.city.CityRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateCityUseCaseImpl implements CreateCityUseCase {
    private final CityRepository cityRepository;

    @Override
    public CityId execute(String name) {
        City city = City.of(name);
        cityRepository.save(city);
        return city.getId();
    }
}
