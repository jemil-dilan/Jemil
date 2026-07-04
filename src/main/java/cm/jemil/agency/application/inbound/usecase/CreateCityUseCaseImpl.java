package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.city.City;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.city.CityName;
import cm.jemil.agency.domain.city.CityRegion;
import cm.jemil.agency.domain.city.CityRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateCityUseCaseImpl implements CreateCityUseCase {
    private final CityRepository cityRepository;

    @Override
    public CityId execute(Command command) {
        City city = City.of(command.getName(), command.getRegion());
        cityRepository.save(city);
        return city.getId();
    }

    public record Command(String name, String region) {
        public CityName getName() {
            return new CityName(name);
        }

        public CityRegion getRegion() {
            return new CityRegion(region);
        }
    }
}
