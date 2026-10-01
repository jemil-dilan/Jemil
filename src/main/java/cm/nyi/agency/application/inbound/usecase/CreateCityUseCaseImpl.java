package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.city.City;
import cm.nyi.agency.domain.city.CityId;
import cm.nyi.agency.domain.city.CityName;
import cm.nyi.agency.domain.city.CityRegion;
import cm.nyi.agency.domain.city.CityRepository;
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
