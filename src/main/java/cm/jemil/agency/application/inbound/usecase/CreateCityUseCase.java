package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.city.CityId;

public interface CreateCityUseCase {
    CityId execute(String name);
}
